package com.drabarz.karolina.testplatformrunner.api

import com.drabarz.karolina.testplatformrunner.service.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*


@CrossOrigin(origins = ["http://localhost:3000", "http://192.168.0.80:3000"], allowCredentials = "true")
@RestController
class TestPlatformApi(val loginService: LoginService,
                      val authHelper: TestPlatformApiHelper,
                      val stageService: StageService,
                      val projectService: ProjectService,
                      val groupService: GroupService,
                      val integrationService: IntegrationService) {

    @PostMapping("/login")
    fun logIn(@RequestBody loginRequest: LoginRequest): LoginResponse {
        return loginService.loginUser(loginRequest)
    }

    @GetMapping("/projects")
    fun getProjectsList(@RequestHeader headers: HttpHeaders): ProjectResponse {
        authHelper.isLecturerOrThrow(headers)
        return ProjectResponse(projectService.getProjects())
    }

    @PostMapping("/project")
    fun addProject(
            @RequestHeader headers: HttpHeaders,
            @RequestParam("projectName") projectName: String): String {
        authHelper.isLecturerOrThrow(headers)
        return projectService.addProject(projectName)
    }

    @DeleteMapping("/{projectName}")
    fun deleteProject(
            @RequestHeader headers: HttpHeaders,
            @PathVariable("projectName") projectName: String): String {
        authHelper.isLecturerOrThrow(headers)
        return projectService.deleteProject(projectName)
    }

    @PostMapping("/project/description")
    fun addProjectDescription(
            @RequestHeader headers: HttpHeaders,
            @RequestParam("file") uploadedFile: MultipartFile,
            @RequestParam("projectName") projectName: String): String {
        authHelper.isLecturerOrThrow(headers)
        return projectService.addProjectDescription(uploadedFile, projectName)
    }

    @PostMapping("/project/environment")
    fun addProjectEnvironment(
            @RequestHeader headers: HttpHeaders,
            @RequestParam("file") uploadedFile: MultipartFile,
            @RequestParam("projectName") projectName: String): String {
        authHelper.isLecturerOrThrow(headers)
        return projectService.addProjectEnvironment(uploadedFile, projectName)
    }

    @GetMapping("/{projectName}/description")
    @ResponseBody
    fun downloadProjectDescriptionFile(@PathVariable("projectName") projectName: String): ResponseEntity<*> {
        return createFileResponse(projectService.getProjectDescription(projectName))
    }

    @GetMapping("/{projectName}/environment")
    @ResponseBody
    fun downloadProjectEnvironmentFile(@PathVariable("projectName") projectName: String): ResponseEntity<*> {
        return createFileResponse(projectService.getProjectEnvironment(projectName))
    }

    @GetMapping("/{projectName}/{stageName}/description")
    @ResponseBody
    fun downloadStageDescriptionFile(
            @PathVariable("projectName") projectName: String,
            @PathVariable("stageName") stageName: String): ResponseEntity<*> {
        return createFileResponse(stageService.getStageDescription(projectName, stageName))
    }

    @PostMapping("/stage/description")
    fun addStageDescription(
            @RequestHeader headers: HttpHeaders,
            @RequestParam("file") uploadedFile: MultipartFile,
            @RequestParam("projectName") projectName: String,
            @RequestParam("stageName") stageName: String): String {
        authHelper.isLecturerOrThrow(headers)
        return stageService.addStageDescription(uploadedFile, projectName, stageName)
    }

    @PostMapping("/stage/startDate")
    fun editStageStartDate(
            @RequestHeader headers: HttpHeaders,
            @RequestParam("startDate") startDate: String?,
            @RequestParam("projectName") projectName: String,
            @RequestParam("stageName") stageName: String): String {
        authHelper.isLecturerOrThrow(headers)
        return stageService.editStageDate(projectName, stageName, startDate, "START")
    }

    @PostMapping("/stage/endDate")
    fun editStageEndDate(
            @RequestHeader headers: HttpHeaders,
            @RequestParam("endDate") endDate: String?,
            @RequestParam("projectName") projectName: String,
            @RequestParam("stageName") stageName: String): String {
        authHelper.isLecturerOrThrow(headers)
        return stageService.editStageDate(projectName, stageName, endDate, "END")
    }

    @PostMapping("/stage/comment")
    fun addStageComment(
            @RequestHeader headers: HttpHeaders,
            @RequestParam("comment") comment: String?,
            @RequestParam("projectName") projectName: String,
            @RequestParam("stageName") stageName: String): String {
        authHelper.isLecturerOrThrow(headers)
        return stageService.editComment(projectName, stageName, comment)
    }

    @GetMapping("/{projectName}/stages")
    fun getStagesList(@PathVariable("projectName") projectName: String): StagesResponse {
        return StagesResponse(
                projectService.getProjectDescriptionName(projectName),
                projectService.getProjectEnvironmentName(projectName),
                stageService.getStages(projectName))
    }

    @PostMapping("/stage")
    fun addStage(
            @RequestHeader headers: HttpHeaders,
            @RequestParam("projectName") projectName: String,
            @RequestParam("stageName") stageName: String,
            @RequestParam("startDate") startDate: String?,
            @RequestParam("endDate") endDate: String?): String {
        authHelper.isLecturerOrThrow(headers)
        return stageService.addStage(projectName, stageName, startDate, endDate)
    }

    @DeleteMapping("/stage/{projectName}/{stageName}")
    fun deleteStage(
            @RequestHeader headers: HttpHeaders,
            @PathVariable("projectName") projectName: String,
            @PathVariable("stageName") stageName: String): String {
        authHelper.isLecturerOrThrow(headers)
        return stageService.deleteStage(projectName, stageName)
    }

    @PostMapping("/stage/testCase")
    fun uploadStageTestCase(
            @RequestHeader headers: HttpHeaders,
            @RequestParam("input") inputFile: MultipartFile,
            @RequestParam("output") outputFile: MultipartFile,
            @RequestParam("projectName") projectName: String,
            @RequestParam("stageName") stageName: String,
            @RequestParam("testCaseName") testCaseName: String): String {
        authHelper.isLecturerOrThrow(headers)
        return stageService.saveTestCase(inputFile, outputFile, projectName, stageName, testCaseName)
    }

    @PostMapping("/stage/testCase/parameters")
    fun editStageTestCaseParameters(
            @RequestHeader headers: HttpHeaders,
            @RequestParam("parameters") parameters: String?,
            @RequestParam("projectName") projectName: String,
            @RequestParam("stageName") stageName: String,
            @RequestParam("testCaseName") testCaseName: String): String {
        authHelper.isLecturerOrThrow(headers)
        return stageService.editTestCaseParameters(projectName, stageName, testCaseName, parameters)
    }

    @GetMapping("/stage/{projectName}/{stageName}/{testCaseName}/{fileType}")
    @ResponseBody
    fun downloadStageTestCaseFile(
            @RequestHeader headers: HttpHeaders,
            @PathVariable("projectName") projectName: String,
            @PathVariable("stageName") stageName: String,
            @PathVariable("testCaseName") testCaseName: String,
            @PathVariable("fileType") fileType: String): ResponseEntity<*> {
        return createFileResponse(stageService.getTestCaseFile(projectName, stageName, testCaseName, fileType))
    }

    @PostMapping("/stage/{projectName}/{stageName}/{testCaseName}/{fileType}")
    fun uploadStageTestCaseFile(
            @RequestHeader headers: HttpHeaders,
            @PathVariable("projectName") projectName: String,
            @PathVariable("stageName") stageName: String,
            @PathVariable("testCaseName") testCaseName: String,
            @PathVariable("fileType") fileType: String,
            @RequestParam("file") file: MultipartFile): String {
        authHelper.isLecturerOrThrow(headers)
        return stageService.uploadTestCaseFile(projectName, stageName, testCaseName, fileType, file)
    }

    @DeleteMapping("/stage/{projectName}/{stageName}/{testCaseName}")
    fun deleteStageTestCase(
            @RequestHeader headers: HttpHeaders,
            @PathVariable("projectName") projectName: String,
            @PathVariable("stageName") stageName: String,
            @PathVariable("testCaseName") testCaseName: String): String {
        authHelper.isLecturerOrThrow(headers)
        return stageService.deleteTestCase(projectName, stageName, testCaseName)
    }

    @PostMapping("/group")
    fun addGroup(
            @RequestHeader headers: HttpHeaders,
            @RequestParam("groupName") groupName: String,
            @RequestParam("projectName") projectName: String): String {
        authHelper.isLecturerOrThrow(headers)
        return groupService.addGroup(groupName, projectName)
    }

    @DeleteMapping("/group")
    fun deleteGroup(
            @RequestHeader headers: HttpHeaders,
            @RequestParam("groupName") groupName: String,
            @RequestParam("projectName") projectName: String): String {
        authHelper.isLecturerOrThrow(headers)
        return groupService.deleteGroup(groupName, projectName)
    }

    @PostMapping("/group/student")
    fun addStudent(
            @RequestHeader headers: HttpHeaders,
            @RequestParam("studentName") studentName: String,
            @RequestParam("groupName") groupName: String,
            @RequestParam("projectName") projectName: String): String {
        return groupService.addStudentToGroup(projectName, groupName, studentName)
    }

    @DeleteMapping("/group/student")
    fun removeStudentFromGroup(
            @RequestHeader headers: HttpHeaders,
            @RequestParam("studentName") studentName: String,
            @RequestParam("groupName") groupName: String,
            @RequestParam("projectName") projectName: String): String {
        authHelper.isLecturerOrThrow(headers)
        return groupService.removeStudentFromGroup(projectName, groupName, studentName)
    }

    @GetMapping("/{projectName}/groups")
    fun getGroups(@PathVariable("projectName") projectName: String): GroupsResponse {
        return groupService.getGroups(projectName)
    }

    @GetMapping("/{projectName}/group/{groupName}")
    fun getStudentsAssignedToGroup(
            @PathVariable("projectName") projectName: String,
            @PathVariable("groupName") groupName: String): List<String> {
        return groupService.getStudentsAssignedToGroup(projectName, groupName)
    }

    @PostMapping("/{projectName}/groups")
    fun addGroups(
            @RequestHeader headers: HttpHeaders,
            @RequestParam("file") uploadedFile: MultipartFile,
            @PathVariable("projectName") projectName: String): String {
        authHelper.isLecturerOrThrow(headers)

        val groupsDao = ObjectMapper().readValue<GroupsDao>(uploadedFile.bytes)

        return groupService.addGroups(groupsDao, projectName)
    }

    @PostMapping("/{projectName}/integrations")
    fun addIntegration(
            @RequestHeader headers: HttpHeaders,
            @PathVariable("projectName") projectName: String,
            @RequestBody integration: IntegrationDao): String {
        authHelper.isLecturerOrThrow(headers)
        return integrationService.addIntegration(projectName, integration.name, integration.integrationStages)
    }

    @GetMapping("/{projectName}/integrations")
    fun getIntegrations(@PathVariable("projectName") projectName: String): IntegrationsDao {
        return integrationService.getIntegrations(projectName)
    }

    @PostMapping("/integration/comment")
    fun addIntegrationComment(
            @RequestHeader headers: HttpHeaders,
            @RequestParam("comment") comment: String?,
            @RequestParam("projectName") projectName: String,
            @RequestParam("integrationName") integrationName: String): String {
        authHelper.isLecturerOrThrow(headers)
        return integrationService.editComment(projectName, integrationName, comment)
    }

    @DeleteMapping("/integration/{projectName}/{integrationName}")
    fun deleteIntegration(
            @RequestHeader headers: HttpHeaders,
            @PathVariable("projectName") projectName: String,
            @PathVariable("integrationName") integrationName: String): String {
        authHelper.isLecturerOrThrow(headers)
        return integrationService.deleteIntegration(projectName, integrationName)
    }

    @PostMapping("/integration/testCase")
    fun uploadIntegrationTestCase(
            @RequestHeader headers: HttpHeaders,
            @RequestParam("input") inputFile: MultipartFile,
            @RequestParam("output") outputFile: MultipartFile,
            @RequestParam("projectName") projectName: String,
            @RequestParam("integrationName") integrationName: String,
            @RequestParam("testCaseName") testCaseName: String): String {
        return integrationService.saveTestCase(inputFile, outputFile, projectName, integrationName, testCaseName)
    }

    @PostMapping("/integration/testCase/parameters")
    fun editIntegrationTestCaseParameters(
            @RequestHeader headers: HttpHeaders,
            @RequestParam("parameters") parameters: String?,
            @RequestParam("projectName") projectName: String,
            @RequestParam("integrationName") integrationName: String,
            @RequestParam("testCaseName") testCaseName: String): String {
        authHelper.isLecturerOrThrow(headers)
        return integrationService.editTestCaseParameters(projectName, integrationName, testCaseName, parameters)
    }

    @GetMapping("/integration/{projectName}/{integrationName}/{testCaseName}/{fileType}")
    @ResponseBody
    fun downloadIntegrationTestCaseFile(
            @PathVariable("projectName") projectName: String,
            @PathVariable("integrationName") integrationName: String,
            @PathVariable("testCaseName") testCaseName: String,
            @PathVariable("fileType") fileType: String): ResponseEntity<*> {
        return createFileResponse(integrationService.getTestCaseFile(projectName, integrationName, testCaseName, fileType))
    }

    @PostMapping("/integration/{projectName}/{integrationName}/{testCaseName}/{fileType}")
    fun uploadIntegrationTestCaseFile(
            @RequestHeader headers: HttpHeaders,
            @PathVariable("projectName") projectName: String,
            @PathVariable("integrationName") integrationName: String,
            @PathVariable("testCaseName") testCaseName: String,
            @PathVariable("fileType") fileType: String,
            @RequestParam("file") file: MultipartFile): String {
        authHelper.isLecturerOrThrow(headers)
        return integrationService.uploadTestCaseFile(projectName, integrationName, testCaseName, fileType, file)
    }

    @DeleteMapping("/integration/{projectName}/{integrationName}/{testCaseName}")
    fun deleteIntegrationTestCase(
            @RequestHeader headers: HttpHeaders,
            @PathVariable("projectName") projectName: String,
            @PathVariable("integrationName") integrationName: String,
            @PathVariable("testCaseName") testCaseName: String): String {
        authHelper.isLecturerOrThrow(headers)
        return integrationService.deleteTestCase(projectName, integrationName, testCaseName)
    }
}

data class EmailDao(val email: String)
data class LoginRequest(val code: String)
data class LoginResponse(val token: String, val userName: String, val accessRights: String)
data class GithubAuthRequest(val client_id: String, val client_secret: String, val code: String)
data class GithubAuthResponse(val access_token: String)
class ProjectResponse(val projects: List<String>)
class StagesResponse(val projectDescription: String?, val projectEnvironment: String?, val stages: List<StageDao>)
class StageDao(val stageName: String, val stageDescription: String?, val startDate: String?, val endDate: String?, val comment: String?, val testCases: List<TestCase>)
class TestCase(val testCaseName: String, val parameters: String?, val inputFileName: String?, val outputFileName: String?)
class GroupsResponse(val groups: List<Group>)
class Group(val groupName: String, val projectName: String, val students: List<String>)
data class GroupsDao(val groups: List<GroupDao> = ArrayList())
data class GroupDao(val name: String = "", val students: List<String> = ArrayList())
data class IntegrationsDao(val integrations: List<IntegrationDao> = ArrayList())
data class IntegrationDao(val name: String, val integrationStages: List<IntegrationStageDao>, val comment: String?, val testCases: List<TestCase>?)
data class IntegrationStageDao(val name: String, val orderNumber: Int, val stageName: String)