package com.drabarz.karolina.testplatformrunner

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile


@SpringBootApplication
class TestPlatformRunnerApplication

fun main(args: Array<String>) {
    TestPlatformRunnerApplication::class.java.getResource("/static/Dockerfile").readText().let { print(it) }
    runApplication<TestPlatformRunnerApplication>(*args)
}

@RestController
class TestPlatformApi {

    val jarService = JarService()
    val testCaseService = TestCaseService()

    @PostMapping("/jar")
    fun uploadJar(@RequestParam("file") uploadedFile: MultipartFile, @RequestParam("stageName") stageName: String): TestResponse {
        val originalFileName = uploadedFile.originalFilename

        jarService.saveFile(uploadedFile)

        return jarService.runJar(originalFileName, stageName)
    }

    @PostMapping("/testCase")
    fun uploadTestCase(
            @RequestParam("input") inputFile: MultipartFile,
            @RequestParam("output") outputFile: MultipartFile,
            @RequestParam("stageName") stageName: String,
            @RequestParam("testCaseName") testCaseName: String): TestResponse {
        return testCaseService.saveTestCase(inputFile, outputFile, stageName, testCaseName)
    }

    @PostMapping("/stage")
    fun addStage(@RequestParam("stageName") stageName: String): TestResponse {
        return testCaseService.addStage(stageName)
    }
}
