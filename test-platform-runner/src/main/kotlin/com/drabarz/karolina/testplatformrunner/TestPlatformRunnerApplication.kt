package com.drabarz.karolina.testplatformrunner

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import org.springframework.context.annotation.AnnotationConfigApplicationContext




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
    fun uploadJar(
            @RequestParam("file") uploadedFile: MultipartFile,
            @RequestParam("projectName") projectName: String,
            @RequestParam("stageName") stageName: String): List<TestResponse> {
        val originalFileName = uploadedFile.originalFilename

        jarService.saveFile(uploadedFile)

        return jarService.runJar(originalFileName, projectName, stageName)
    }

    @GetMapping("/projects")
    fun getProjectsList() : List<String> {
        return testCaseService.getProjects()
    }

    @PostMapping("/project")
    fun addProject(@RequestParam("projectName") projectName: String): TestResponse {
        return testCaseService.addProject(projectName)
    }

    @PostMapping("/stage")
    fun addStage(@RequestParam("projectName") projectName: String, @RequestParam("stageName") stageName: String): TestResponse {
        return testCaseService.addStage(projectName, stageName)
    }

    @PostMapping("/testCase")
    fun uploadTestCase(
            @RequestParam("input") inputFile: MultipartFile,
            @RequestParam("output") outputFile: MultipartFile,
            @RequestParam("projectName") projectName: String,
            @RequestParam("stageName") stageName: String,
            @RequestParam("testCaseName") testCaseName: String): TestResponse {
        return testCaseService.saveTestCase(inputFile, outputFile, projectName, stageName, testCaseName)
    }
}
