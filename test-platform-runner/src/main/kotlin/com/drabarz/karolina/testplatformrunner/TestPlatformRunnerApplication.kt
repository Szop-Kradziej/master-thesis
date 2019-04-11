package com.drabarz.karolina.testplatformrunner

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@SpringBootApplication
class TestPlatformRunnerApplication

fun main(args: Array<String>) {
    TestPlatformRunnerApplication::class.java.getResource("/static/Dockerfile").readText().let { print(it) }
    runApplication<TestPlatformRunnerApplication>(*args)
}

@CrossOrigin(origins = ["http://localhost:3000"], allowCredentials = "true")
@RestController
class TestPlatformApi(val jarService: JarService, val testCaseService: TestCaseService) {

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
    fun getProjectsList(): ProjectResponse {
        return ProjectResponse(testCaseService.getProjects())
    }

    @PostMapping("/project")
    fun addProject(@RequestParam("projectName") projectName: String): String {
        return testCaseService.addProject(projectName)
    }

    @GetMapping("/{projectName}/stages")
    fun getStagesList(@PathVariable("projectName") projectName: String): StagesResponse {
        return StagesResponse(testCaseService.getStages(projectName))
    }

    @PostMapping("/stage")
    fun addStage(@RequestParam("projectName") projectName: String, @RequestParam("stageName") stageName: String): String {
        return testCaseService.addStage(projectName, stageName)
    }

    @PostMapping("/testCase")
    fun uploadTestCase(
            @RequestParam("input") inputFile: MultipartFile,
            @RequestParam("output") outputFile: MultipartFile,
            @RequestParam("projectName") projectName: String,
            @RequestParam("stageName") stageName: String,
            @RequestParam("testCaseName") testCaseName: String): String {
        return testCaseService.saveTestCase(inputFile, outputFile, projectName, stageName, testCaseName)
    }
}

class ProjectResponse(val projects: List<String>)
class StagesResponse(val stages: List<Stage>)
class Stage(val stageName: String, val testCases: List<String>)
