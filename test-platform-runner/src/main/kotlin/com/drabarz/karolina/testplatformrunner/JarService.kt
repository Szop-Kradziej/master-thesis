package com.drabarz.karolina.testplatformrunner

import org.hibernate.annotations.common.util.impl.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
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

    val pathProvider: PathProv = PathProvider()

    fun saveFile(uploadedFile: MultipartFile) {
        val outputFile = File(pathProvider.projectPath, uploadedFile.originalFilename)
        uploadedFile.transferTo(outputFile)
    }

    fun runJar(jarName: String?, projectName: String, stageName: String): List<TestResponse> {
        val testCasesNames = testCaseService.getTestCasesNames(projectName, stageName)

        if(testCasesNames.isEmpty()) {
            throw java.lang.RuntimeException("Error. There are no test cases for stage $stageName")
        }

        return testCasesNames.map {
            val container = containerFactory.createContainerWithFilesBinded(projectName, stageName, it, jarName)
            containerService.runTestCase(container)
            checkCorrectness(projectName, stageName, it)
        }
    }

    fun checkCorrectness(projectName: String, stageName: String, testCaseName: String): TestResponse {
        try {
            val expectedOutput = File("${pathProvider.projectPath}/$projectName/$stageName/$testCaseName/output").reader().readText()
            val testOutput = JarService::class.java.getResource("/static/output.txt").readText()

            if (testOutput.trim() == expectedOutput.trim()) {
                return Success()
            }

            return Error("Error: \n Actual: $testOutput \n Expected: $expectedOutput")
        } catch (e: RuntimeException) {
            return Error(e.message!!)
        }
    }
}

sealed class TestResponse
class Success : TestResponse()
class Error(val message: String) : TestResponse()

@Component
class ContainerFactory {

    val pathProvider: PathProv = PathProvider()

    fun createContainerWithFilesBinded(projectName: String, stageName: String, testCaseName: String, jarName: String?): KGenericContainer {

        return KGenericContainer(
                ImageFromDockerfile()
                        .withFileFromClasspath("Dockerfile", "static/Dockerfile")
        )
                .withCopyFileToContainer(MountableFile.forHostPath("${pathProvider.jarPath}/$jarName"), "/home/example.jar")
                .withFileSystemBind("${pathProvider.projectPath}/$projectName/$stageName/$testCaseName/input", "/home/input.txt", BindMode.READ_ONLY)
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
