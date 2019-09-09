package com.drabarz.karolina.testplatformrunner.service

import com.drabarz.karolina.testplatformrunner.api.IntegrationTestCase
import com.drabarz.karolina.testplatformrunner.api.TestCase
import com.drabarz.karolina.testplatformrunner.model.Integration
import com.drabarz.karolina.testplatformrunner.service.helper.IntegrationPathProvider
import com.drabarz.karolina.testplatformrunner.service.helper.PathProvider
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

    private val stagesTestCaseService = TestCaseService(stagePathProvider)
    private val integrationTestCaseService = TestCaseService(integrationPathProvider)

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
            val parametersFile = getParameters(stagePathProvider.getTaskTestCaseParametersDir(projectName, stageName, it.testCaseName))
            val outputFile = File(stagePathProvider.getStudentOutputDir(groupName, projectName, stageName).apply { mkdir() }, "output").apply { delete() }.apply { createNewFile() }
            val container = containerFactory.createContainerWithFilesBinded(environmentDir, inputFile.absolutePath, parametersFile.absolutePath, outputFile.absolutePath, "${binDir.absolutePath}/$binName")
            containerService.runTestCase(container)
                    .also { logs -> saveLogsToStageResultFile(groupName, logs, projectName, stageName, it.testCaseName) }
            val expectedOutput = stagePathProvider.getTaskTestCaseFileDir(projectName, stageName, it.testCaseName, "output").listFiles().first().readText()
            checkCorrectness(it.testCaseName, outputFile, expectedOutput)
        }
    }

    private fun getParameters(parametersDir: File): File {
        if (!parametersDir.exists()) {
            parametersDir.mkdir()
        }

        if (parametersDir.list().isEmpty()) {
            File(parametersDir.absolutePath, PathProvider.PARAMETERS).createNewFile()
        }

        return parametersDir.listFiles().first()
    }

    private fun saveLogsToStageResultFile(groupName: String, logs: String, projectName: String, stageName: String, testCaseName: String) {
        val logsDir = stagePathProvider.getStudentLogsDir(groupName, projectName, stageName)
        logsDir.mkdirs()

        val file = File(logsDir, testCaseName)

        file.writeText(logs)
    }

    private fun checkCorrectness(testCaseName: String, outputFile: File, expectedOutput: String): TestResponse {
        try {
            val testOutput = outputFile.readText()

            if (testOutput.trim() == expectedOutput.trim()) {
                return TestResponse(testCaseName, SUCCESS_STATUS)
            }

            return TestResponse(testCaseName, FAILURE_STATUS, "Error: \n Actual: $testOutput \n Expected: $expectedOutput")
        } catch (e: RuntimeException) {
            return TestResponse(testCaseName, FAILURE_STATUS, e.message!!)
        }
    }

    fun runBins(groupName: String, projectName: String, integrationName: String): List<TestResponse> {
        log.info("Running program of group: $groupName for integration: $integrationName in project: $projectName")
        val integrationStages = integrationService.getIntegrationStages(projectName, integrationName)
        val testCases = integrationTestCaseService.getIntegrationTestCases(projectName, integrationName, integrationStages.size)

        if (testCases.isEmpty()) {
            log.warn("There are no test cases for integration: $integrationName in project: $projectName")
            throw java.lang.RuntimeException("Error. There are no test cases for integration $integrationName")
        }

        log.info("Getting all binaries for assigned stagpenguins_simulationpenguins_simulationpenguins_simulationpenguins_simulationpenguins_simulationpenguins_simulationes for integration: $integrationName in project: $projectName")

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
            val parametersFiles = getIntegrationParameters(integrationPathProvider.getTaskTestCaseParametersDir(projectName, integrationName, testCase.testCaseName), binPaths.size)
            val outputFile = binPaths.zip(parametersFiles).fold(inputFile) { accumulatedInputFile, (binFile, parametersFile) ->
                val binPath = binFile.absolutePath
                generateOutputFile(environmentDir, groupName, projectName, integrationName, accumulatedInputFile, parametersFile, binPath, testCase)
            }

            val expectedOutput = integrationPathProvider.getTaskTestCaseFileDir(projectName, integrationName, testCase.testCaseName, "output").listFiles().first().readText()

            checkCorrectness(testCase.testCaseName, outputFile, expectedOutput)
        }
    }

    private fun getIntegrationParameters(parametersDir: File, stageCount: Int): List<File> {
        if (!parametersDir.exists()) {
            parametersDir.mkdir()
        }
        return (0 until stageCount).map {
            val stageParametersDir = File(parametersDir, "stage_$it")
            if (!stageParametersDir.exists()) {
                stageParametersDir.mkdir()
            }
            if (stageParametersDir.list().isEmpty()) {
                File(stageParametersDir, PathProvider.PARAMETERS).createNewFile()
            }
            stageParametersDir.listFiles().first()
        }
    }

    private fun generateOutputFile(environmentDir: File, groupName: String, projectName: String, integrationName: String, inputFile: File, parametersFile: File, binPath: String, testCase: IntegrationTestCase): File {
        val outputFile = File(integrationPathProvider.getStudentOutputDir(groupName, projectName, integrationName).apply { mkdirs() }, "output").apply { delete() }.apply { createNewFile() }

        val container = containerFactory.createContainerWithFilesBinded(environmentDir, inputFile.absolutePath, parametersFile.absolutePath, outputFile.absolutePath, binPath)
        containerService.runTestCase(container)
                .also { logs -> saveLogsToIntegrationResultFile(groupName, logs, projectName, integrationName, testCase.testCaseName) }
        return outputFile
    }

    private fun saveLogsToIntegrationResultFile(groupName: String, logs: String, projectName: String, integrationName: String, testCaseName: String) {
        val logsDir = integrationPathProvider.getStudentLogsDir(groupName, projectName, integrationName)
        logsDir.mkdirs()

        val file = File(logsDir, testCaseName)

        file.appendText(logs)
    }

    companion object {
        val log = LoggerFactory.logger(BinService::class.java)
        const val SUCCESS_STATUS = "SUCCESS"
        const val FAILURE_STATUS = "FAILURE"
        const val NO_RUN_STATUS = "NO_RUN"
    }
}

data class TestResponse constructor(val testCaseName: String, val status: String = "NO RUN", val message: String? = null)

@Component
class ContainerFactory {

    fun createContainerWithFilesBinded(environmentDir: File, inputFilePath: String, parametersFilePath: String, outputFilePath: String, binPath: String): KGenericContainer {
        return KGenericContainer(
                ImageFromDockerfile()
                        .withFileFromFile("Dockerfile", environmentDir.listFiles().first())
        )
                .withCopyFileToContainer(MountableFile.forHostPath(binPath), "/home/app")
                .withFileSystemBind(inputFilePath, "/home/input.txt", BindMode.READ_ONLY)
                .withFileSystemBind(parametersFilePath, "/home/parameters.txt", BindMode.READ_ONLY)
                .withFileSystemBind(outputFilePath, "/home/output.txt", BindMode.READ_WRITE)
    }
}

@Component
class ContainerService {

    fun runTestCase(container: KGenericContainer): String {
        container.start()

        val startTime = System.currentTimeMillis()
        while (container.isRunning && startTime + 5000 > System.currentTimeMillis()) {
            Thread.sleep(10)
        }

        val toStringConsumer = ToStringConsumer()
        container.followOutput(toStringConsumer, OutputFrame.OutputType.STDERR)

        val containerLogs = "Container id: " + container.containerId + "\n" +
                "Program output: " + toStringConsumer.toUtf8String() + "\n" +
                "Container logs: " + container.logs + "\n"
        log.info(containerLogs)

        container.stop()

        return containerLogs
    }

    companion object {
        val log = LoggerFactory.logger(ContainerService::class.java)
    }
}

class KGenericContainer(imageName: ImageFromDockerfile) : GenericContainer<KGenericContainer>(imageName)
