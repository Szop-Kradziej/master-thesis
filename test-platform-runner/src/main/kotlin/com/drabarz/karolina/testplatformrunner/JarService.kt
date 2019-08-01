package com.drabarz.karolina.testplatformrunner

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
class JarService(
        val pathProvider: StagePathProvider,
        val containerFactory: ContainerFactory,
        val containerService: ContainerService) {

    private final val testCaseService = TestCaseService(pathProvider)


    fun runJar(projectName: String, stageName: String): List<TestResponse> {
        val testCases = testCaseService.getTestCases(projectName, stageName)

        if (testCases.isEmpty()) {
            throw java.lang.RuntimeException("Error. There are no test cases for stage $stageName")
        }

        val jarPath = pathProvider.getStudentBinDir(projectName, stageName)

        if (!jarPath.exists() || jarPath.list().size != 1) {
            throw java.lang.RuntimeException("Invalid number of binaries or no binary")
        }

        val jarName = jarPath.list().first()

        return testCases.map {
            val inputFilePath = pathProvider.getTaskTestCaseFileDir(projectName, stageName, it.testCaseName, "input").listFiles().first().absolutePath
            val outputFilePath = pathProvider.getStudentOutputDir(projectName, stageName)
            outputFilePath.mkdir()
            File(outputFilePath, "output").createNewFile()
            val container = containerFactory.createContainerWithFilesBinded(inputFilePath, File(outputFilePath, "output").absolutePath, "${jarPath.absolutePath}/$jarName")
            containerService.runTestCase(container)
                    .also { logs -> saveLogsToResultFile(logs, projectName, stageName, it.testCaseName) }
            checkCorrectness(projectName, stageName, it.testCaseName)
        }
    }

    private fun saveLogsToResultFile(logs: String, projectName: String, stageName: String, testCaseName: String) {
        val logsDir = pathProvider.getStudentLogsDir(projectName, stageName)
        logsDir.mkdirs()

        val file = File(logsDir, testCaseName);

        file.writeText(logs)
    }

    fun checkCorrectness(projectName: String, stageName: String, testCaseName: String): TestResponse {
        try {
            val expectedOutput = pathProvider.getTaskTestCaseFileDir(projectName, stageName, testCaseName, "output").listFiles().first().readText()
            val testOutput = File(pathProvider.getStudentOutputDir(projectName, stageName), "output").readText()

            if (testOutput.trim() == expectedOutput.trim()) {
                return TestResponse(testCaseName, "SUCCESS")
            }

            return TestResponse(testCaseName, "FAILURE", "Error: \n Actual: $testOutput \n Expected: $expectedOutput")
        } catch (e: RuntimeException) {
            return TestResponse(testCaseName, "FAILURE", e.message!!)
        }
    }

    fun runJars(projectName: String, integrationName: String): List<TestResponse> {
        return emptyList()
    }

    companion object {
        val log = LoggerFactory.logger(JarService::class.java);
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

    fun createContainerWithFilesBinded(inputFilePath: String, outputFilePath: String, jarPath: String): KGenericContainer {

        return KGenericContainer(
                ImageFromDockerfile()
                        .withFileFromClasspath("Dockerfile", "static/Dockerfile")
        )
                .withCopyFileToContainer(MountableFile.forHostPath(jarPath), "/home/example.jar")
                .withFileSystemBind(inputFilePath, "/home/input.txt", BindMode.READ_ONLY)
                .withFileSystemBind(outputFilePath, "/home/output.txt", BindMode.READ_WRITE)
    }
}

@Component
class ContainerService {

    fun runTestCase(container: KGenericContainer): String {
        val log = LoggerFactory.logger(this.javaClass)

        container.start()

        Thread.sleep(1000)

        val toStringConsumer = ToStringConsumer()
        container.followOutput(toStringConsumer, OutputFrame.OutputType.STDERR)

        val containerLogs = container.containerId + "\n" + toStringConsumer.toUtf8String() + "\n" + container.logs
        log.info(containerLogs)

        container.stop()

        return containerLogs
    }
}

class KGenericContainer(imageName: ImageFromDockerfile) : GenericContainer<KGenericContainer>(imageName)
