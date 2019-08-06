package com.drabarz.karolina.testplatformrunner.api

import com.drabarz.karolina.testplatformrunner.service.GroupService
import com.drabarz.karolina.testplatformrunner.service.IntegrationService
import com.drabarz.karolina.testplatformrunner.service.ProjectService
import com.drabarz.karolina.testplatformrunner.service.StageService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.ArrayList

@CrossOrigin(origins = ["http://localhost:3000", "http://192.168.0.80:3000"], allowCredentials = "true")
@RestController
class TestPlatformApi(val stageService: StageService,
                      val projectService: ProjectService,
                      val groupService: GroupService,
                      val integrationService: IntegrationService) {

    @GetMapping("/projects")
    fun getProjectsList(): ProjectResponse {
        return ProjectResponse(projectService.getProjects())
    }

    @PostMapping("/project")
    fun addProject(@RequestParam("projectName") projectName: String): String {
        return projectService.addProject(projectName)
    }

    @DeleteMapping("/{projectName}")
    fun deleteProject(@PathVariable("projectName") projectName: String): String {
        return projectService.deleteProject(projectName)
    }

    @PostMapping("/project/description")
    fun addProjectDescription(
            @RequestParam("file") uploadedFile: MultipartFile,
            @RequestParam("projectName") projectName: String): String {
        return projectService.addProjectDescription(uploadedFile, projectName)
    }

    @PostMapping("/project/environment")
    fun addProjectEnvironment(
            @RequestParam("file") uploadedFile: MultipartFile,
            @RequestParam("projectName") projectName: String): String {
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
            @RequestParam("file") uploadedFile: MultipartFile,
            @RequestParam("projectName") projectName: String,
            @RequestParam("stageName") stageName: String): String {
        return stageService.addStageDescription(uploadedFile, projectName, stageName)
    }

    @PostMapping("/stage/startDate")
    fun editStageStartDate(
            @RequestParam("startDate") startDate: String?,
            @RequestParam("projectName") projectName: String,
            @RequestParam("stageName") stageName: String): String {
        return stageService.editStageDate(projectName, stageName, startDate, "START")
    }

    @PostMapping("/stage/endDate")
    fun editStageEndDate(
            @RequestParam("endDate") endDate: String?,
            @RequestParam("projectName") projectName: String,
            @RequestParam("stageName") stageName: String): String {
        return stageService.editStageDate(projectName, stageName, endDate, "END")
    }

    @PostMapping("/stage/pointsNumber")
    fun editStagePointsNumber(
            @RequestParam("pointsNumber") pointsNumber: String?,
            @RequestParam("projectName") projectName: String,
            @RequestParam("stageName") stageName: String): String {
        return stageService.editStagePointsNumber(projectName, stageName, pointsNumber)
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
            @RequestParam("projectName") projectName: String,
            @RequestParam("stageName") stageName: String,
            @RequestParam("startDate") startDate: String?,
            @RequestParam("endDate") endDate: String?,
            @RequestParam("pointsNumber") pointsNumber: String?): String {
        return stageService.addStage(projectName, stageName, startDate, endDate, pointsNumber)
    }

    @DeleteMapping("/stage/{projectName}/{stageName}")
    fun deleteStage(@PathVariable("projectName") projectName: String,
                    @PathVariable("stageName") stageName: String): String {
        return stageService.deleteStage(projectName, stageName)
    }

    @PostMapping("/stage/testCase")
    fun uploadStageTestCase(
            @RequestParam("input") inputFile: MultipartFile,
            @RequestParam("output") outputFile: MultipartFile,
            @RequestParam("projectName") projectName: String,
            @RequestParam("stageName") stageName: String,
            @RequestParam("testCaseName") testCaseName: String): String {
        return stageService.saveTestCase(inputFile, outputFile, projectName, stageName, testCaseName)
    }

    @PostMapping("/stage/testCase/parameters")
    fun editStageTestCaseParameters(
            @RequestParam("parameters") parameters: String?,
            @RequestParam("projectName") projectName: String,
            @RequestParam("stageName") stageName: String,
            @RequestParam("testCaseName") testCaseName: String): String {
        return stageService.editTestCaseParameters(projectName, stageName, testCaseName, parameters)
    }

    @GetMapping("/stage/{projectName}/{stageName}/{testCaseName}/{fileType}")
    @ResponseBody
    fun downloadStageTestCaseFile(
            @PathVariable("projectName") projectName: String,
            @PathVariable("stageName") stageName: String,
            @PathVariable("testCaseName") testCaseName: String,
            @PathVariable("fileType") fileType: String): ResponseEntity<*> {
        return createFileResponse(stageService.getTestCaseFile(projectName, stageName, testCaseName, fileType))
    }

    @PostMapping("/stage/{projectName}/{stageName}/{testCaseName}/{fileType}")
    fun uploadStageTestCaseFile(
            @PathVariable("projectName") projectName: String,
            @PathVariable("stageName") stageName: String,
            @PathVariable("testCaseName") testCaseName: String,
            @PathVariable("fileType") fileType: String,
            @RequestParam("file") file: MultipartFile): String {
        return stageService.uploadTestCaseFile(projectName, stageName, testCaseName, fileType, file)
    }

    @DeleteMapping("/stage/{projectName}/{stageName}/{testCaseName}")
    fun deleteStageTestCase(
            @PathVariable("projectName") projectName: String,
            @PathVariable("stageName") stageName: String,
            @PathVariable("testCaseName") testCaseName: String): String {
        return stageService.deleteTestCase(projectName, stageName, testCaseName)
    }

    @PostMapping("/group")
    fun addGroup(
            @RequestParam("groupName") groupName: String,
            @RequestParam("projectName") projectName: String): String {
        return groupService.addGroup(groupName, projectName)
    }

    @DeleteMapping("/group")
    fun deleteGroup(
            @RequestParam("groupName") groupName: String,
            @RequestParam("projectName") projectName: String): String {
        return groupService.deleteGroup(groupName, projectName)
    }

    @PostMapping("/group/student")
    fun addStudent(
            @RequestParam("studentName") studentName: String,
            @RequestParam("groupName") groupName: String,
            @RequestParam("projectName") projectName: String): String {
        return groupService.addStudentToGroup(projectName, groupName, studentName)
    }

    @DeleteMapping("/group/student")
    fun removeStudentFromGroup(
            @RequestParam("studentName") studentName: String,
            @RequestParam("groupName") groupName: String,
            @RequestParam("projectName") projectName: String): String {
        return groupService.removeStudentFromGroup(projectName, groupName, studentName)
    }

    @GetMapping("/{projectName}/groups")
    fun getGroups(@PathVariable("projectName") projectName: String): GroupsResponse {
        return groupService.getGroups(projectName)
    }

    @PostMapping("/{projectName}/groups")
    fun addGroups(
            @RequestParam("file") uploadedFile: MultipartFile,
            @PathVariable("projectName") projectName: String): String {
        val groupsDao = ObjectMapper().readValue<GroupsDao>(uploadedFile.bytes)

        return groupService.addGroups(groupsDao, projectName)
    }

    @PostMapping("/{projectName}/integrations")
    fun addIntegration(
            @PathVariable("projectName") projectName: String,
            @RequestBody integration: IntegrationDao): String {
        return integrationService.addIntegration(projectName, integration.name, integration.integrationStages)
    }

    @GetMapping("/{projectName}/integrations")
    fun getIntegrations(@PathVariable("projectName") projectName: String): IntegrationsDao {
        return integrationService.getIntegrations(projectName)
    }

    @DeleteMapping("/integration/{projectName}/{integrationName}")
    fun deleteIntegration(
            @PathVariable("projectName") projectName: String,
            @PathVariable("integrationName") integrationName: String): String {
        return integrationService.deleteIntegration(projectName, integrationName)
    }

    @PostMapping("/integration/testCase")
    fun uploadIntegrationTestCase(
            @RequestParam("input") inputFile: MultipartFile,
            @RequestParam("output") outputFile: MultipartFile,
            @RequestParam("projectName") projectName: String,
            @RequestParam("integrationName") integrationName: String,
            @RequestParam("testCaseName") testCaseName: String): String {
        return integrationService.saveTestCase(inputFile, outputFile, projectName, integrationName, testCaseName)
    }

    @PostMapping("/integration/testCase/parameters")
    fun editIntegrationTestCaseParameters(
            @RequestParam("parameters") parameters: String?,
            @RequestParam("projectName") projectName: String,
            @RequestParam("integrationName") integrationName: String,
            @RequestParam("testCaseName") testCaseName: String): String {
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
            @PathVariable("projectName") projectName: String,
            @PathVariable("integrationName") integrationName: String,
            @PathVariable("testCaseName") testCaseName: String,
            @PathVariable("fileType") fileType: String,
            @RequestParam("file") file: MultipartFile): String {
        return integrationService.uploadTestCaseFile(projectName, integrationName, testCaseName, fileType, file)
    }

    @DeleteMapping("/integration/{projectName}/{integrationName}/{testCaseName}")
    fun deleteIntegrationTestCase(
            @PathVariable("projectName") projectName: String,
            @PathVariable("integrationName") integrationName: String,
            @PathVariable("testCaseName") testCaseName: String): String {
        return integrationService.deleteTestCase(projectName, integrationName, testCaseName)
    }
}

class ProjectResponse(val projects: List<String>)
class StagesResponse(val projectDescription: String?, val projectEnvironment: String?, val stages: List<StageDao>)
class StageDao(val stageName: String, val stageDescription: String?, val startDate: String?, val endDate: String?, val pointsNumber: String?, val testCases: List<TestCase>)
class TestCase(val testCaseName: String, val parameters: String?, val inputFileName: String?, val outputFileName: String?)
class GroupsResponse(val groups: List<Group>)
class Group(val groupName: String, val projectName: String, val students: List<String>)
data class GroupsDao(val groups: List<GroupDao> = ArrayList())
data class GroupDao(val name: String = "", val students: List<String> = ArrayList())
data class IntegrationsDao(val integrations: List<IntegrationDao> = ArrayList())
data class IntegrationDao(val name: String, val integrationStages: List<IntegrationStageDao>, val testCases: List<TestCase>?)
data class IntegrationStageDao(val name: String, val orderNumber: Int, val stageName: String)