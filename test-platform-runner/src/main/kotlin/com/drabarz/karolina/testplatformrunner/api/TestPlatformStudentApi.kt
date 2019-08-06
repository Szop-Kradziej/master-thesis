package com.drabarz.karolina.testplatformrunner.api

import com.drabarz.karolina.testplatformrunner.service.FileType
import com.drabarz.karolina.testplatformrunner.service.StudentService
import com.drabarz.karolina.testplatformrunner.service.TestResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile


@CrossOrigin(origins = ["http://localhost:3000", "http://192.168.0.80:3000"], allowCredentials = "true")
@RestController
class TestPlatformStudentApi(val studentService: StudentService) {

    @PostMapping("/student/upload/bin")
    fun uploadJar(
            @RequestParam("file") uploadedFile: MultipartFile,
            @RequestParam("projectName") projectName: String,
            @RequestParam("stageName") stageName: String): String {
        studentService.saveFile(projectName, stageName, uploadedFile, FileType.BINARY)

        return "200"
    }

    @GetMapping("/student/{projectName}/{stageName}/bin")
    fun downloadJar(
            @PathVariable("projectName") projectName: String,
            @PathVariable("stageName") stageName: String): ResponseEntity<*> {
        return createFileResponse(studentService.getJar(projectName, stageName))
    }

    @PostMapping("/student/upload/report")
    fun uploadReport(
            @RequestParam("file") uploadedFile: MultipartFile,
            @RequestParam("projectName") projectName: String,
            @RequestParam("stageName") stageName: String): String {
        studentService.saveFile(projectName, stageName, uploadedFile, FileType.REPORT)

        return "200"
    }

    @PostMapping("/student/upload/code")
    fun uploadCode(
            @RequestParam("codeLink") codeLink: String,
            @RequestParam("projectName") projectName: String,
            @RequestParam("stageName") stageName: String): String {
        return studentService.saveCodeLink(projectName, stageName, codeLink)
    }

    @PostMapping("/student/stage/run")
    fun runStageJar(
            @RequestParam("projectName") projectName: String,
            @RequestParam("stageName") stageName: String): List<TestResponse> {
        return studentService.runStageTests(projectName, stageName)
    }

    @PostMapping("/student/integration/run")
    fun runIntegrationJar(
            @RequestParam("projectName") projectName: String,
            @RequestParam("integrationName") integrationName: String): List<TestResponse> {
        return studentService.runIntegrationTests(projectName, integrationName)
    }

    @GetMapping("/student/{projectName}/{stageName}/report")
    fun downloadReport(
            @PathVariable("projectName") projectName: String,
            @PathVariable("stageName") stageName: String): ResponseEntity<*> {
        return createFileResponse(studentService.getReport(projectName, stageName))
    }

    @GetMapping("/student/stage/{projectName}/{stageName}/{testCaseName}/logs")
    fun downloadStudentStageLogsFile(
            @PathVariable("projectName") projectName: String,
            @PathVariable("stageName") stageName: String,
            @PathVariable("testCaseName") testCaseName: String): ResponseEntity<*> {
        return createFileResponse(studentService.getStageLogsFile(projectName, stageName, testCaseName))
    }

    @GetMapping("/student/{projectName}/stages")
    fun getStudentStagesList(@PathVariable("projectName") projectName: String): StudentStagesResponse {
        return StudentStagesResponse(studentService.getStudentStages(projectName))
    }

    @GetMapping("/student/{projectName}/integrations")
    fun getStudentIntegrationsList(@PathVariable("projectName") projectName: String): StudentIntegrationsResponse {
        return StudentIntegrationsResponse(studentService.getStudentIntegrations(projectName))
    }

    @GetMapping("/student/integration/{projectName}/{integrationName}/{testCaseName}/logs")
    fun downloadStudentIntegrationLogsFile(
            @PathVariable("projectName") projectName: String,
            @PathVariable("integrationName") integrationName: String,
            @PathVariable("testCaseName") testCaseName: String): ResponseEntity<*> {
        return createFileResponse(studentService.getIntegrationLogsFile(projectName, integrationName, testCaseName))
    }
}

class StudentStagesResponse(val stages: List<StudentStage>)
class StudentStage(val stageName: String, val binaryName: String?, val reportName: String?, val testCases: List<TestCaseWithResult>, val passedTestCasesCount: Int, val allTestCasesCount: Int, val startDate: String?, val endDate: String?, val pointsNumber: String?, val totalPointsNumber: String?, val codeLink: String?, val enable: Boolean)
class StudentIntegrationsResponse(val integrations: List<StudentIntegration>)
class StudentIntegration(val integrationName: String, val integrationStages: List<IntegrationStageDao>, val testCases: List<TestCaseWithResult>, val passedTestCasesCount: Int, val allTestCasesCount: Int, val enable: Boolean)
class TestCaseWithResult(val testCaseName: String, val parameters: String?, val status: String = "NO RUN", val message: String?, val isLogsFile: Boolean = false)



