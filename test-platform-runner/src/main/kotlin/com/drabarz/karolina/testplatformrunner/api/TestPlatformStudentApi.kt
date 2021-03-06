package com.drabarz.karolina.testplatformrunner.api

import com.drabarz.karolina.testplatformrunner.service.FileType
import com.drabarz.karolina.testplatformrunner.service.StudentService
import com.drabarz.karolina.testplatformrunner.service.TestResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@CrossOrigin(origins = ["http://localhost:3000", "http://192.168.0.80:3000", "http://34.65.81.82:3000"], allowCredentials = "true")
@RestController
//TODO: check access rights
class TestPlatformStudentApi(
        val studentService: StudentService,
        val authHelper: TestPlatformApiHelper
) {

    @GetMapping("/student/projects")
    fun getProjectsList(@RequestHeader headers: HttpHeaders): ProjectResponse {
        val userName = authHelper.getUserNameFromRequestHeader(headers)
        return ProjectResponse(studentService.getStudentProjects(userName))
    }

    @PostMapping("/student/upload/bin")
    fun uploadBin(
            @RequestHeader headers: HttpHeaders,
            @RequestParam("file") uploadedFile: MultipartFile,
            @RequestParam("projectName") projectName: String,
            @RequestParam("stageName") stageName: String): String {
        val userName = authHelper.getUserNameFromRequestHeader(headers)
        studentService.saveFile(userName, projectName, stageName, uploadedFile, FileType.BINARY)

        return "200"
    }

    @GetMapping("/student/{projectName}/{stageName}/bin")
    fun downloadBin(
            @RequestHeader headers: HttpHeaders,
            @PathVariable("projectName") projectName: String,
            @PathVariable("stageName") stageName: String): ResponseEntity<*> {
        val userName = authHelper.getUserNameFromRequestHeader(headers)
        return createFileResponse(studentService.getBin(userName, projectName, stageName))
    }

    @PostMapping("/student/upload/report")
    fun uploadReport(
            @RequestHeader headers: HttpHeaders,
            @RequestParam("file") uploadedFile: MultipartFile,
            @RequestParam("projectName") projectName: String,
            @RequestParam("stageName") stageName: String): String {
        val userName = authHelper.getUserNameFromRequestHeader(headers)
        studentService.saveFile(userName, projectName, stageName, uploadedFile, FileType.REPORT)

        return "200"
    }

    @PostMapping("/student/upload/code")
    fun uploadCode(
            @RequestHeader headers: HttpHeaders,
            @RequestParam("codeLink") codeLink: String,
            @RequestParam("projectName") projectName: String,
            @RequestParam("stageName") stageName: String): String {
        val userName = authHelper.getUserNameFromRequestHeader(headers)
        return studentService.saveCodeLink(userName, projectName, stageName, codeLink)
    }

    @PostMapping("/student/stage/run")
    fun runStageBin(
            @RequestHeader headers: HttpHeaders,
            @RequestParam("projectName") projectName: String,
            @RequestParam("stageName") stageName: String): List<TestResponse> {
        val userName = authHelper.getUserNameFromRequestHeader(headers)
        return studentService.runStageTests(userName, projectName, stageName)
    }

    @PostMapping("/student/integration/run")
    fun runIntegrationBin(
            @RequestHeader headers: HttpHeaders,
            @RequestParam("projectName") projectName: String,
            @RequestParam("integrationName") integrationName: String): List<TestResponse> {
        val userName = authHelper.getUserNameFromRequestHeader(headers)
        return studentService.runIntegrationTests(userName, projectName, integrationName)
    }

    @GetMapping("/student/{projectName}/{stageName}/report")
    fun downloadReport(
            @RequestHeader headers: HttpHeaders,
            @PathVariable("projectName") projectName: String,
            @PathVariable("stageName") stageName: String): ResponseEntity<*> {
        val userName = authHelper.getUserNameFromRequestHeader(headers)
        return createFileResponse(studentService.getReport(userName, projectName, stageName))
    }

    @GetMapping("/student/stage/{projectName}/{stageName}/{testCaseName}/logs")
    fun downloadStudentStageLogsFile(
            @RequestHeader headers: HttpHeaders,
            @PathVariable("projectName") projectName: String,
            @PathVariable("stageName") stageName: String,
            @PathVariable("testCaseName") testCaseName: String): ResponseEntity<*> {
        val userName = authHelper.getUserNameFromRequestHeader(headers)
        return createFileResponse(studentService.getStageLogsFile(userName, projectName, stageName, testCaseName))
    }

    @GetMapping("/student/{projectName}/group")
    fun getGroup(
            @RequestHeader headers: HttpHeaders,
            @PathVariable("projectName") projectName: String): StudentGroup {
        val userName = authHelper.getUserNameFromRequestHeader(headers)
        return studentService.getStudentGroup(userName, projectName)
    }

    @GetMapping("/student/{projectName}/stages")
    fun getStudentStagesList(
            @RequestHeader headers: HttpHeaders,
            @PathVariable("projectName") projectName: String): StudentStagesResponse {
        val userName = authHelper.getUserNameFromRequestHeader(headers)
        return StudentStagesResponse(studentService.getStudentStages(userName, projectName))
    }

    @GetMapping("/student/{projectName}/integrations")
    fun getStudentIntegrationsList(
            @RequestHeader headers: HttpHeaders,
            @PathVariable("projectName") projectName: String): StudentIntegrationsResponse {
        val userName = authHelper.getUserNameFromRequestHeader(headers)
        return StudentIntegrationsResponse(studentService.getStudentIntegrations(userName, projectName))
    }

    @GetMapping("/student/integration/{projectName}/{integrationName}/{testCaseName}/logs")
    fun downloadStudentIntegrationLogsFile(
            @RequestHeader headers: HttpHeaders,
            @PathVariable("projectName") projectName: String,
            @PathVariable("integrationName") integrationName: String,
            @PathVariable("testCaseName") testCaseName: String): ResponseEntity<*> {
        val userName = authHelper.getUserNameFromRequestHeader(headers)
        return createFileResponse(studentService.getIntegrationLogsFile(userName, projectName, integrationName, testCaseName))
    }
}

class StudentStagesResponse(val stages: List<StudentStage>)
class StudentStage(val stageName: String, val binaryName: String?, val reportName: String?, val testCases: List<TestCaseWithResult>, val passedTestCasesCount: Int, val allTestCasesCount: Int, val startDate: String?, val endDate: String?, val successfulGroups: Int, val totalGroupsNumber: Int, val codeLink: String?, val enable: Boolean)
class TestCaseWithResult(val testCaseName: String, val parameters: Boolean, val status: String = "NO RUN", val message: String?, val isLogsFile: Boolean = false)
class StudentIntegrationsResponse(val integrations: List<StudentIntegration>)
class StudentIntegration(val integrationName: String, val integrationStages: List<IntegrationStageDao>, val testCases: List<IntegrationTestCaseWithResult>, val passedTestCasesCount: Int, val allTestCasesCount: Int, val successfulGroups: Int, val totalGroupsNumber: Int, val enable: Boolean)
data class IntegrationTestCaseWithResult(val testCaseName: String, val parameters: List<Boolean>, val status: String = "NO RUN", val message: String?, val isLogsFile: Boolean = false)
class StudentGroup(val name: String, val students: List<String>)



