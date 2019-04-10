package com.drabarz.karolina.testplatformrunner

import org.hibernate.annotations.common.util.impl.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import org.testcontainers.containers.BindMode
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.output.OutputFrame
import org.testcontainers.containers.output.ToStringConsumer
import org.testcontainers.images.builder.ImageFromDockerfile
import org.testcontainers.utility.MountableFile
import java.io.File

@Component
class JarService {

    val containerFactory = ContainerFactory()
    val containerService = ContainerService()
    val testCaseService = TestCaseService()

    fun saveFile(uploadedFile: MultipartFile) {
        val outputFile = File(PATH_PREFIX, uploadedFile.originalFilename)
        uploadedFile.transferTo(outputFile)
    }

    fun runJar(jarName: String?, projectName: String, stageName: String): TestResponse {
        val testCaseName = testCaseService.getTestCaseName(projectName, stageName)
        val container = containerFactory.createContainerWithFilesBinded(projectName, stageName, testCaseName, jarName)
        containerService.runTestCase(container)
        return checkCorrectness(projectName, stageName, testCaseName)
    }

    fun checkCorrectness(projectName: String, stageName:String, testCaseName: String): TestResponse {
        try {
            val expectedOutput = File("$PATH_PREFIX/$projectName/$stageName/$testCaseName/output").reader().readText()
            val testOutput = JarService::class.java.getResource("/static/output.txt").readText()

            if (testOutput.trim() == expectedOutput.trim()) {
                return Success()
            }

            return Error("Error: \n Actual: $testOutput \n Expected: $expectedOutput")
        } catch (e: RuntimeException) {
            return Error(e.message!!)
        }
    }

    companion object {
        val PATH_PREFIX = "/home/karolina/MGR/tests";
    }
}

sealed class TestResponse
class Success : TestResponse()
class Error(val message: String) : TestResponse()

@Component
class ContainerFactory {

    fun createContainerWithFilesBinded(projectName: String, stageName: String, testCaseName: String, jarName: String?): KGenericContainer {

        return KGenericContainer(
                ImageFromDockerfile()
                        .withFileFromClasspath("Dockerfile", "static/Dockerfile")
        )
                .withCopyFileToContainer(MountableFile.forHostPath("${JarService.PATH_PREFIX}/$jarName"), "/home/example.jar")
                .withFileSystemBind("${JarService.PATH_PREFIX}/$projectName/$stageName/$testCaseName/input", "/home/input.txt", BindMode.READ_ONLY)
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
