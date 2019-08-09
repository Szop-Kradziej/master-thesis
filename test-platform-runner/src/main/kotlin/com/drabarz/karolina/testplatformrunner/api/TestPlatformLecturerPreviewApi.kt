package com.drabarz.karolina.testplatformrunner.api

import com.drabarz.karolina.testplatformrunner.service.FileType
import com.drabarz.karolina.testplatformrunner.service.GroupResultService
import com.drabarz.karolina.testplatformrunner.service.TestResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@CrossOrigin(origins = ["http://localhost:3000", "http://192.168.0.80:3000"], allowCredentials = "true")
@RestController
//TODO: check access rights
class TestPlatformLecturerPreviewApi(val groupResultService: GroupResultService) {

    @PostMapping("/preview/{groupName}/upload/bin")
    fun uploadBin(
            @RequestHeader headers: HttpHeaders,
            @PathVariable("groupName") groupName: String,
            @RequestParam("file") uploadedFile: MultipartFile,
            @RequestParam("projectName") projectName: String,
            @RequestParam("stageName") stageName: String): String {
        val userName = getUserNameFromRequestHeader(headers)
        groupResultService.saveFile(userName, groupName, projectName, stageName, uploadedFile, FileType.BINARY)

        return "200"
    }

    @GetMapping("/preview/{groupName}/{projectName}/{stageName}/bin")
    fun downloadBin(
            @RequestHeader headers: HttpHeaders,
            @PathVariable("groupName") groupName: String,
            @PathVariable("projectName") projectName: String,
            @PathVariable("stageName") stageName: String): ResponseEntity<*> {
        val userName = getUserNameFromRequestHeader(headers)
        return createFileResponse(groupResultService.getBin(groupName, projectName, stageName))
    }

    @PostMapping("/preview/{groupName}/upload/report")
    fun uploadReport(
            @RequestHeader headers: HttpHeaders,
            @PathVariable("groupName") groupName: String,
            @RequestParam("file") uploadedFile: MultipartFile,
            @RequestParam("projectName") projectName: String,
            @RequestParam("stageName") stageName: String): String {
        val userName = getUserNameFromRequestHeader(headers)
        groupResultService.saveFile(userName, groupName, projectName, stageName, uploadedFile, FileType.REPORT)

        return "200"
    }

    @PostMapping("/preview/{groupName}/upload/code")
    fun uploadCode(
            @RequestHeader headers: HttpHeaders,
            @PathVariable("groupName") groupName: String,
            @RequestParam("codeLink") codeLink: String,
            @RequestParam("projectName") projectName: String,
            @RequestParam("stageName") stageName: String): String {
        val userName = getUserNameFromRequestHeader(headers)
        return groupResultService.saveCodeLink(userName, groupName, projectName, stageName, codeLink)
    }

    @PostMapping("/preview/{groupName}/stage/run")
    fun runStageBin(
            @RequestHeader headers: HttpHeaders,
            @PathVariable("groupName") groupName: String,
            @RequestParam("projectName") projectName: String,
            @RequestParam("stageName") stageName: String): List<TestResponse> {
        val userName = getUserNameFromRequestHeader(headers)
        return groupResultService.runStageTests(userName, groupName, projectName, stageName)
    }

    @PostMapping("/preview/{groupName}/integration/run")
    fun runIntegrationBin(
            @RequestHeader headers: HttpHeaders,
            @PathVariable("groupName") groupName: String,
            @RequestParam("projectName") projectName: String,
            @RequestParam("integrationName") integrationName: String): List<TestResponse> {
        val userName = getUserNameFromRequestHeader(headers)
        return groupResultService.runIntegrationTests(userName, groupName, projectName, integrationName)
    }

    @GetMapping("/preview/{groupName}/{projectName}/{stageName}/report")
    fun downloadReport(
            @RequestHeader headers: HttpHeaders,
            @PathVariable("groupName") groupName: String,
            @PathVariable("projectName") projectName: String,
            @PathVariable("stageName") stageName: String): ResponseEntity<*> {
        val userName = getUserNameFromRequestHeader(headers)
        return createFileResponse(groupResultService.getReport(groupName, projectName, stageName))
    }

    @GetMapping("/preview/{groupName}/stage/{projectName}/{stageName}/{testCaseName}/logs")
    fun downloadStudentStageLogsFile(
            @RequestHeader headers: HttpHeaders,
            @PathVariable("groupName") groupName: String,
            @PathVariable("projectName") projectName: String,
            @PathVariable("stageName") stageName: String,
            @PathVariable("testCaseName") testCaseName: String): ResponseEntity<*> {
        val userName = getUserNameFromRequestHeader(headers)
        return createFileResponse(groupResultService.getStageLogsFile(groupName, projectName, stageName, testCaseName))
    }

    @GetMapping("/preview/{groupName}/stage/{projectName}/{stageName}/statistics")
    fun downloadStudentStageStatisticsFile(
            @RequestHeader headers: HttpHeaders,
            @PathVariable("groupName") groupName: String,
            @PathVariable("projectName") projectName: String,
            @PathVariable("stageName") stageName: String): ResponseEntity<*> {
        val userName = getUserNameFromRequestHeader(headers)
        return createFileResponse(groupResultService.getStageStatisticsFile(groupName, projectName, stageName))
    }

    @GetMapping("/preview/{groupName}/{projectName}/stages")
    fun getStudentStagesList(
            @RequestHeader headers: HttpHeaders,
            @PathVariable("groupName") groupName: String,
            @PathVariable("projectName") projectName: String): StudentPreviewStagesResponse {
        val userName = getUserNameFromRequestHeader(headers)
        return StudentPreviewStagesResponse(groupResultService.getStudentPreviewStages(groupName, projectName))
    }

    @GetMapping("/preview/{groupName}/{projectName}/integrations")
    fun getStudentIntegrationsList(
            @RequestHeader headers: HttpHeaders,
            @PathVariable("groupName") groupName: String,
            @PathVariable("projectName") projectName: String): StudentPreviewIntegrationsResponse {
        val userName = getUserNameFromRequestHeader(headers)
        return StudentPreviewIntegrationsResponse(groupResultService.getStudentPreviewIntegrations(groupName, projectName))
    }

    @GetMapping("/preview/{groupName}/integration/{projectName}/{integrationName}/statistics")
    fun downloadStudentIntegrationStatisticsFile(
            @RequestHeader headers: HttpHeaders,
            @PathVariable("groupName") groupName: String,
            @PathVariable("projectName") projectName: String,
            @PathVariable("integrationName") integrationName: String): ResponseEntity<*> {
        val userName = getUserNameFromRequestHeader(headers)
        return createFileResponse(groupResultService.getIntegrationStatisticsFile(groupName, projectName, integrationName))
    }

    @GetMapping("/preview/{groupName}/integration/{projectName}/{integrationName}/{testCaseName}/logs")
    fun downloadStudentIntegrationLogsFile(
            @RequestHeader headers: HttpHeaders,
            @PathVariable("groupName") groupName: String,
            @PathVariable("projectName") projectName: String,
            @PathVariable("integrationName") integrationName: String,
            @PathVariable("testCaseName") testCaseName: String): ResponseEntity<*> {
        val userName = getUserNameFromRequestHeader(headers)
        return createFileResponse(groupResultService.getIntegrationLogsFile(groupName, projectName, integrationName, testCaseName))
    }
}

class StudentPreviewStagesResponse(val stages: List<StudentPreviewStage>)
class StudentPreviewStage(val stageName: String, val binaryName: String?, val reportName: String?, val testCases: List<TestCaseWithResult>, val passedTestCasesCount: Int, val allTestCasesCount: Int, val startDate: String?, val endDate: String?, val successfulGroups: Int, val totalGroupsNumber: Int, val codeLink: String?, val statistics: Boolean)
class StudentPreviewIntegrationsResponse(val integrations: List<StudentPreviewIntegration>)
class StudentPreviewIntegration(val integrationName: String, val integrationStages: List<IntegrationStageDao>, val testCases: List<TestCaseWithResult>, val passedTestCasesCount: Int, val allTestCasesCount: Int, val successfulGroups: Int, val totalGroupsNumber: Int, val statistics: Boolean, val enable: Boolean)

