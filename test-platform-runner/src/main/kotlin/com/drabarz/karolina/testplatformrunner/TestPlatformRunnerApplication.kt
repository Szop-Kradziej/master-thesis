package com.drabarz.karolina.testplatformrunner

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.util.StringUtils.getFilename
import org.springframework.web.server.adapter.WebHttpHandlerBuilder.applicationContext
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.GetMapping
import java.io.File
import java.lang.RuntimeException


@SpringBootApplication
class TestPlatformRunnerApplication

fun main(args: Array<String>) {
    TestPlatformRunnerApplication::class.java.getResource("/static/Dockerfile").readText().let { print(it) }
    runApplication<TestPlatformRunnerApplication>(*args)
}

@CrossOrigin(origins = ["http://localhost:3000"], allowCredentials = "true")
@RestController
class TestPlatformApi(val jarService: JarService, val testCaseService: TestCaseService, val applicationContext: ApplicationContext) {

    @PostMapping("/upload")
    fun uploadJar(
            @RequestParam("file") uploadedFile: MultipartFile,
            @RequestParam("projectName") projectName: String,
            @RequestParam("stageName") stageName: String): String {

        jarService.saveFile(projectName, stageName, uploadedFile)

        return "200"
    }

    @PostMapping("/run")
    fun uploadJar(
            @RequestParam("projectName") projectName: String,
            @RequestParam("stageName") stageName: String): List<TestResponse> {

        return jarService.runJar(projectName, stageName)
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

    @GetMapping("/{projectName}/{stageName}/testCases")
    fun getTestCasesList(@PathVariable("projectName") projectName: String, @PathVariable("stageName") stageName: String): TestCasesResponse {
        return TestCasesResponse(testCaseService.getTestCasesNames(projectName, stageName))
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

    @GetMapping("/{projectName}/{stageName}/{testCaseName}/{fileName}")
    @ResponseBody
    fun downloadFile(
            @PathVariable("projectName") projectName: String,
            @PathVariable("stageName") stageName: String,
            @PathVariable("testCaseName") testCaseName: String,
            @PathVariable("fileName") fileName: String): ResponseEntity<*> {
        val file = testCaseService.getTestCaseFile(projectName, stageName, testCaseName, fileName)

        val headers = HttpHeaders()
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.name + "\"")
        headers.add("Access-Control-Expose-Headers", HttpHeaders.CONTENT_DISPOSITION + "," + HttpHeaders.CONTENT_LENGTH)

        return ResponseEntity.ok().headers(headers).body<Any>(file.readBytes())
    }
}

class ProjectResponse(val projects: List<String>)
class StagesResponse(val stages: List<Stage>)
class Stage(val stageName: String, val testCases: List<String>)
class TestCasesResponse(val testCases: List<String>)
