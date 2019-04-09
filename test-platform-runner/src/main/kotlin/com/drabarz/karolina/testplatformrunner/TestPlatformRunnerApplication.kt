package com.drabarz.karolina.testplatformrunner

import org.hibernate.annotations.common.util.impl.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.io.File
import org.testcontainers.containers.BindMode
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.output.OutputFrame
import org.testcontainers.containers.output.ToStringConsumer
import org.testcontainers.images.builder.ImageFromDockerfile
import org.testcontainers.utility.MountableFile
import java.util.*


@SpringBootApplication
class TestPlatformRunnerApplication

fun main(args: Array<String>) {
	TestPlatformRunnerApplication::class.java.getResource("/static/Dockerfile").readText().let{print(it)}
	runApplication<TestPlatformRunnerApplication>(*args)
}

@RestController
class TestPlatformApi {

	val jarService = JarService()
    val testCaseService = TestCaseService()

	@PostMapping("/jar")
	fun uploadJar(@RequestParam("file") uploadedFile: MultipartFile): TestResponse {
		val originalFileName = uploadedFile.originalFilename

		jarService.saveFile(uploadedFile)

		return jarService.runJar(originalFileName)
	}

    @PostMapping("/testCase")
    fun uploadTestCase(@RequestParam("input") inputFile: MultipartFile, @RequestParam("output") outputFile: MultipartFile): String {
        testCaseService.saveTestCase(inputFile, outputFile)

        return "200"
    }
}

@Component
class JarService {

	fun saveFile(uploadedFile: MultipartFile) {
		val outputFile = File(PATH_PREFIX, uploadedFile.originalFilename)
		uploadedFile.transferTo(outputFile)
	}

	fun runJar(originalFileName: String?): TestResponse {
		val log = LoggerFactory.logger(this.javaClass)
        val testCaseName = "example_test_case"

		val container = KGenericContainer(
				ImageFromDockerfile()
						.withFileFromClasspath("Dockerfile", "static/Dockerfile")
		)
				.withCopyFileToContainer(MountableFile.forHostPath("$PATH_PREFIX/$originalFileName"), "/home/example.jar")
				.withFileSystemBind("$PATH_PREFIX/$testCaseName/input.txt", "/home/input.txt", BindMode.READ_ONLY)
				.withClasspathResourceMapping("/static/output.txt", "/home/output.txt", BindMode.READ_WRITE)

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
class TestCaseService {
    fun saveTestCase(inputFile: MultipartFile, outputFile: MultipartFile) {
        val testCaseName = UUID.randomUUID()
        val testCasePath = "$PATH_PREFIX/$testCaseName"

        val dir = File(testCasePath)
        dir.mkdir()

        val savedInputFile = File(testCasePath, INPUT_FILE_NAME)
        inputFile.transferTo(savedInputFile)

        val savedOutputFile = File(testCasePath, OUTPUT_FILE_NAME)
        outputFile.transferTo(savedOutputFile)
    }

    companion object {
        val PATH_PREFIX = "/home/karolina/MGR/tests"
        val INPUT_FILE_NAME = "input"
        val OUTPUT_FILE_NAME = "output"
    }
}

class KGenericContainer(imageName: ImageFromDockerfile) : GenericContainer<KGenericContainer>(imageName)

