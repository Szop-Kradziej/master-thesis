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
	fun uploadJar(@RequestParam("file") uploadedFile: MultipartFile, @RequestParam("testCaseName") testCaseName : String): TestResponse {
		val originalFileName = uploadedFile.originalFilename

		jarService.saveFile(uploadedFile)

		return jarService.runJar(originalFileName, testCaseName)
	}

    @PostMapping("/testCase")
    fun uploadTestCase(@RequestParam("input") inputFile: MultipartFile, @RequestParam("output") outputFile: MultipartFile): String {
        testCaseService.saveTestCase(inputFile, outputFile)

        return "200"
    }
}
