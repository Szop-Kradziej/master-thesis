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
class JarService(val pathProvider: PathProvider, val containerFactory: ContainerFactory, val containerService: ContainerService, val testCaseService: TestCaseService) {

    fun runJar(projectName: String, stageName: String): List<TestResponse> {
        val testCasesNames = testCaseService.getTestCasesNames(projectName, stageName)

        if (testCasesNames.isEmpty()) {
            throw java.lang.RuntimeException("Error. There are no test cases for stage $stageName")
        }

        val jarPath = pathProvider.getStudentBinDir(projectName, stageName)

        if (!jarPath.exists() || jarPath.list().size != 1) {
            throw java.lang.RuntimeException("Invalid number of binaries or no binary")
        }

        val jarName = jarPath.list()[0]

        return testCasesNames.map {
            val container = containerFactory.createContainerWithFilesBinded(projectName, stageName, it, "${jarPath.absolutePath}/$jarName")
            containerService.runTestCase(container)
            checkCorrectness(projectName, stageName, it)
        }
    }

    fun checkCorrectness(projectName: String, stageName: String, testCaseName: String): TestResponse {
        try {
            val expectedOutput = pathProvider.getTestCaseFileDir(projectName, stageName, testCaseName,"output").reader().readText()
            val testOutput = JarService::class.java.getResource("/static/output.txt").readText()

            if (testOutput.trim() == expectedOutput.trim()) {
                return TestResponse(testCaseName, "SUCCESS")
            }

            return TestResponse(testCaseName, "FAILURE", "Error: \n Actual: $testOutput \n Expected: $expectedOutput")
        } catch (e: RuntimeException) {
            return TestResponse(testCaseName, "FAILURE", e.message!!)
        }
    }

    companion object {
        val log = LoggerFactory.logger(JarService::class.java);
    }
}

data class TestResponse constructor (val testCaseName: String, val status: String = "NO RUN", val message: String? = null)
//class Success(val testCaseName: String) : TestResponse() {
//    val status = "SUCCESS"
//}
//class Error(val testCaseName: String, val message: String) : TestResponse() {
//    val status = "FAILURE"
//}

@Component
class ContainerFactory(val pathProvider: PathProvider) {

    fun createContainerWithFilesBinded(projectName: String, stageName: String, testCaseName: String, jarPath: String?): KGenericContainer {

        return KGenericContainer(
                ImageFromDockerfile()
                        .withFileFromClasspath("Dockerfile", "static/Dockerfile")
        )
                .withCopyFileToContainer(MountableFile.forHostPath("$jarPath"), "/home/example.jar")
                .withFileSystemBind(pathProvider.getTestCaseFileDir(projectName, stageName, testCaseName, "input").absolutePath, "/home/input.txt", BindMode.READ_ONLY)
                .withClasspathResourceMapping("/static/output.txt", "/home/output.txt", BindMode.READ_WRITE)
    }
}

@Component
class ContainerService {

    fun runTestCase(container: KGenericContainer) {
        val log = LoggerFactory.logger(this.javaClass)

        container.start()

        Thread.sleep(1000)

        val toStringConsumer = ToStringConsumer()
        container.followOutput(toStringConsumer, OutputFrame.OutputType.STDERR)

        log.info(container.containerId)
        log.info(toStringConsumer.toUtf8String())
        log.info(container.logs)

        container.stop()
    }
}

class KGenericContainer(imageName: ImageFromDockerfile) : GenericContainer<KGenericContainer>(imageName)
