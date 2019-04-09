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

    fun saveFile(uploadedFile: MultipartFile) {
        val outputFile = File(PATH_PREFIX, uploadedFile.originalFilename)
        uploadedFile.transferTo(outputFile)
    }

    fun runJar(originalFileName: String?, testCaseName: String): TestResponse {
        val log = LoggerFactory.logger(this.javaClass)

        val container = containerFactory.createContainerWithFilesBinded(testCaseName, originalFileName)

        container.start()

        Thread.sleep(1000)

        val toStringConsumer = ToStringConsumer()
        container.followOutput(toStringConsumer, OutputFrame.OutputType.STDERR)

        log.info(container.containerId)
        log.info(toStringConsumer.toUtf8String())
        log.info(container.logs)

        container.stop()

        val expectedOutput = File("$PATH_PREFIX/$testCaseName/output.txt").reader().readText()
        val testOutput = JarService::class.java.getResource("/static/output.txt").readText()

        if(checkCorrectness(testOutput, expectedOutput)) {
            return Success()
        }

        return Error("Error: \n Actual: $testOutput \n Expected: $expectedOutput")
    }

    fun checkCorrectness(testOutput: String, expectedOutput: String): Boolean {
        return testOutput.trim() == expectedOutput.trim()
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

    fun createContainerWithFilesBinded(testCaseName: String, jarName: String?): KGenericContainer {

        return KGenericContainer(
                ImageFromDockerfile()
                        .withFileFromClasspath("Dockerfile", "static/Dockerfile")
        )
                .withCopyFileToContainer(MountableFile.forHostPath("${JarService.PATH_PREFIX}/$jarName"), "/home/example.jar")
                .withFileSystemBind("${JarService.PATH_PREFIX}/$testCaseName/input.txt", "/home/input.txt", BindMode.READ_ONLY)
                .withClasspathResourceMapping("/static/output.txt", "/home/output.txt", BindMode.READ_WRITE)
    }
}

class KGenericContainer(imageName: ImageFromDockerfile) : GenericContainer<KGenericContainer>(imageName)
