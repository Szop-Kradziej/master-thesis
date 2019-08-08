package com.drabarz.karolina.testplatformrunner.api

import com.drabarz.karolina.testplatformrunner.service.FileType
import com.drabarz.karolina.testplatformrunner.service.GroupResultService
import com.drabarz.karolina.testplatformrunner.service.StudentService
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
    fun uploadJar(
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
    fun downloadJar(
            @RequestHeader headers: HttpHeaders,
            @PathVariable("groupName") groupName: String,
            @PathVariable("projectName") projectName: String,
            @PathVariable("stageName") stageName: String): ResponseEntity<*> {
        val userName = getUserNameFromRequestHeader(headers)
        return createFileResponse(groupResultService.getJar(groupName, projectName, stageName))
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
    fun runStageJar(
            @RequestHeader headers: HttpHeaders,
            @PathVariable("groupName") groupName: String,
            @RequestParam("projectName") projectName: String,
            @RequestParam("stageName") stageName: String): List<TestResponse> {
        val userName = getUserNameFromRequestHeader(headers)
        return groupResultService.runStageTests(userName, groupName, projectName, stageName)
    }

    @PostMapping("/preview/{groupName}/integration/run")
    fun runIntegrationJar(
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
            @PathVariable("projectName") projectName: String): StudentStagesResponse {
        val userName = getUserNameFromRequestHeader(headers)
        return StudentStagesResponse(groupResultService.getStudentStages(groupName, projectName))
    }

    @GetMapping("/preview/{groupName}/{projectName}/integrations")
    fun getStudentIntegrationsList(
            @RequestHeader headers: HttpHeaders,
            @PathVariable("groupName") groupName: String,
            @PathVariable("projectName") projectName: String): StudentIntegrationsResponse {
        val userName = getUserNameFromRequestHeader(headers)
        return StudentIntegrationsResponse(groupResultService.getStudentIntegrations(groupName, projectName))
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

