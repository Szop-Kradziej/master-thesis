package com.drabarz.karolina.testplatformrunner.service

import com.drabarz.karolina.testplatformrunner.api.TestCase
import com.drabarz.karolina.testplatformrunner.service.helper.IntegrationPathProvider
import com.drabarz.karolina.testplatformrunner.service.helper.StagePathProvider
import org.hibernate.annotations.common.util.impl.LoggerFactory
import org.springframework.stereotype.Component
import org.testcontainers.containers.BindMode
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.output.OutputFrame
import org.testcontainers.containers.output.ToStringConsumer
import org.testcontainers.images.builder.ImageFromDockerfile
import org.testcontainers.utility.MountableFile
import java.io.File

@Component
class BinService(
        val stagePathProvider: StagePathProvider,
        val integrationPathProvider: IntegrationPathProvider,
        val integrationService: IntegrationService,
        val containerFactory: ContainerFactory,
        val containerService: ContainerService) {

    private final val stagesTestCaseService = TestCaseService(stagePathProvider)
    private final val integrationTestCaseService = TestCaseService(integrationPathProvider)


    fun runBin(groupName: String, projectName: String, stageName: String): List<TestResponse> {
        log.info("Running program of group: $groupName for stage: $stageName in project: $projectName")
        val testCases = stagesTestCaseService.getTestCases(projectName, stageName)

        if (testCases.isEmpty()) {
            log.warn("There are no test cases for stage: $stageName in project: $projectName")
            throw java.lang.RuntimeException("Error. There are no test cases for stage $stageName")
        }

        val binDir = stagePathProvider.getStudentBinDir(groupName, projectName, stageName)

        if (!binDir.exists() || binDir.list().size != 1) {
            log.error("There is invalid number of binaries or no binary for group: $groupName for stage: $stageName in project: $projectName")
            throw java.lang.RuntimeException("Invalid number of binaries or no binary")
        }

        val binName = binDir.list().first()

        log.info("Getting environment for project: $projectName")

        val environmentDir = stagePathProvider.getProjectEnvironmentDir(projectName)

        log.info("Running all test cases for binary for group: $groupName for stage: $stageName in project: $projectName")

        return testCases.map {
            val inputFile = stagePathProvider.getTaskTestCaseFileDir(projectName, stageName, it.testCaseName, "input").listFiles().first()
            val outputFile = File(stagePathProvider.getStudentOutputDir(groupName, projectName, stageName).apply { mkdir() }, "output").apply { createNewFile() }
            val container = containerFactory.createContainerWithFilesBinded(environmentDir, inputFile.absolutePath, outputFile.absolutePath, "${binDir.absolutePath}/$binName")
            containerService.runTestCase(container)
                    .also { logs -> saveLogsToStageResultFile(groupName, logs, projectName, stageName, it.testCaseName) }
            val expectedOutput = stagePathProvider.getTaskTestCaseFileDir(projectName, stageName, it.testCaseName, "output").listFiles().first().readText()
            checkCorrectness(it.testCaseName, outputFile, expectedOutput)
        }
    }

    private fun saveLogsToStageResultFile(groupName: String, logs: String, projectName: String, stageName: String, testCaseName: String) {
        val logsDir = stagePathProvider.getStudentLogsDir(groupName, projectName, stageName)
        logsDir.mkdirs()

        val file = File(logsDir, testCaseName);

        file.writeText(logs)
    }

    private fun checkCorrectness(testCaseName: String, outputFile: File, expectedOutput: String): TestResponse {
        try {
            val testOutput = outputFile.readText()

            if (testOutput.trim() == expectedOutput.trim()) {
                return TestResponse(testCaseName, "SUCCESS")
            }

            return TestResponse(testCaseName, "FAILURE", "Error: \n Actual: $testOutput \n Expected: $expectedOutput")
        } catch (e: RuntimeException) {
            return TestResponse(testCaseName, "FAILURE", e.message!!)
        }
    }

    fun runBins(groupName: String, projectName: String, integrationName: String): List<TestResponse> {
        log.info("Running program of group: $groupName for integration: $integrationName in project: $projectName")
        val testCases = integrationTestCaseService.getTestCases(projectName, integrationName)
        val integrationStages = integrationService.getIntegrationStages(projectName, integrationName)

        if (testCases.isEmpty()) {
            log.warn("There are no test cases for integration: $integrationName in project: $projectName")
            throw java.lang.RuntimeException("Error. There are no test cases for integration $integrationName")
        }

        log.info("Getting all binaries for assigned stages for integration: $integrationName in project: $projectName")

        val binPaths = integrationStages.map {
            val binDir = stagePathProvider.getStudentBinDir(groupName, projectName, it.stageName)

            if (!binDir.exists() || binDir.list().size != 1) {
                log.error("There is invalid number of binaries or no binary for group: $groupName for stage: ${it.stageName} in project: $projectName")
                throw java.lang.RuntimeException("Invalid number of binaries or no binary")
            }

            File(binDir, binDir.list().first())
        }

        log.info("Getting environment for project: $projectName")

        val environmentDir = stagePathProvider.getProjectEnvironmentDir(projectName)

        integrationPathProvider.getStudentLogsDir(groupName, projectName, integrationName).listFiles()?.forEach { it.delete() }

        log.info("Running all test cases for binaries for group: $groupName for integration: $integrationName in project: $projectName")

        return testCases.map { testCase ->
            val inputFile = integrationPathProvider.getTaskTestCaseFileDir(projectName, integrationName, testCase.testCaseName, "input").listFiles().first()
            val outputFile = binPaths.fold(inputFile) { inputFile, it ->
                val binPath = it.absolutePath
                generateOutputFile(environmentDir, groupName, projectName, integrationName, inputFile, binPath, testCase)
            }

            val expectedOutput = integrationPathProvider.getTaskTestCaseFileDir(projectName, integrationName, testCase.testCaseName, "output").listFiles().first().readText()

            checkCorrectness(testCase.testCaseName, outputFile, expectedOutput)
        }
    }

    private fun generateOutputFile(environmentDir: File, groupName: String, projectName: String, integrationName: String, inputFile: File, binPath: String, testCase: TestCase): File {
        val outputFile = File(integrationPathProvider.getStudentOutputDir(groupName, projectName, integrationName).apply { mkdirs() }, "output").apply { createNewFile() }

        val container = containerFactory.createContainerWithFilesBinded(environmentDir, inputFile.absolutePath, outputFile.absolutePath, binPath)
        containerService.runTestCase(container)
                .also { logs -> saveLogsToIntegrationResultFile(groupName, logs, projectName, integrationName, testCase.testCaseName) }
        return outputFile
    }

    private fun saveLogsToIntegrationResultFile(groupName: String, logs: String, projectName: String, integrationName: String, testCaseName: String) {
        val logsDir = integrationPathProvider.getStudentLogsDir(groupName, projectName, integrationName)
        logsDir.mkdirs()

        val file = File(logsDir, testCaseName);

        file.appendText(logs)
    }

    companion object {
        val log = LoggerFactory.logger(BinService::class.java);
    }
}

data class TestResponse constructor(val testCaseName: String, val status: String = "NO RUN", val message: String? = null)
//class Success(val testCaseName: String) : TestResponse() {
//    val status = "SUCCESS"
//}
//class Error(val testCaseName: String, val message: String) : TestResponse() {
//    val status = "FAILURE"
//}

@Component
class ContainerFactory {

    fun createContainerWithFilesBinded(environmentDir: File, inputFilePath: String, outputFilePath: String, binPath: String): KGenericContainer {

        return KGenericContainer(
                ImageFromDockerfile()
                        .withFileFromFile("Dockerfile", environmentDir.listFiles().first())
        )//TODO: Fix example.jar - provide bin name
                .withCopyFileToContainer(MountableFile.forHostPath(binPath), "/home/example.jar")
                .withFileSystemBind(inputFilePath, "/home/input.txt", BindMode.READ_ONLY)
                .withFileSystemBind(outputFilePath, "/home/output.txt", BindMode.READ_WRITE)
    }
}

@Component
class ContainerService {

    fun runTestCase(container: KGenericContainer): String {
        container.start()

        Thread.sleep(1000)

        val toStringConsumer = ToStringConsumer()
        container.followOutput(toStringConsumer, OutputFrame.OutputType.STDERR)

        val containerLogs = container.containerId + "\n" + toStringConsumer.toUtf8String() + "\n" + container.logs + "\n"
        log.info(containerLogs)

        container.stop()

        return containerLogs
    }

    companion object {
        val log = LoggerFactory.logger(ContainerService::class.java)
    }
}

class KGenericContainer(imageName: ImageFromDockerfile) : GenericContainer<KGenericContainer>(imageName)
