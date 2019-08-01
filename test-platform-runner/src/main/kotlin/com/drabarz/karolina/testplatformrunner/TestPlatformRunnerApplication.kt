package com.drabarz.karolina.testplatformrunner

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.GetMapping
import java.io.File
import java.util.*

@SpringBootApplication
class TestPlatformRunnerApplication

fun main(args: Array<String>) {
    TestPlatformRunnerApplication::class.java.getResource("/static/Dockerfile").readText().let { print(it) }
    runApplication<TestPlatformRunnerApplication>(*args)
}

@CrossOrigin(origins = ["http://localhost:3000", "http://192.168.0.80:3000"], allowCredentials = "true")
@RestController
class TestPlatformApi(val studentService: StudentService,
                      val stageService: StageService,
                      val projectService: ProjectService,
                      val groupService: GroupService,
                      val integrationService: IntegrationService,
                      val applicationContext: ApplicationContext) {

    @PostMapping("/upload/bin")
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

    @PostMapping("/upload/report")
    fun uploadReport(
            @RequestParam("file") uploadedFile: MultipartFile,
            @RequestParam("projectName") projectName: String,
            @RequestParam("stageName") stageName: String): String {
        studentService.saveFile(projectName, stageName, uploadedFile, FileType.REPORT)

        return "200"
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

    @PostMapping("/upload/code")
    fun uploadCode(
            @RequestParam("codeLink") codeLink: String,
            @RequestParam("projectName") projectName: String,
            @RequestParam("stageName") stageName: String): String {
        return studentService.saveCodeLink(projectName, stageName, codeLink)
    }

    @PostMapping("/run")
    fun runJar(
            @RequestParam("projectName") projectName: String,
            @RequestParam("stageName") stageName: String): List<TestResponse> {
        return studentService.runJar(projectName, stageName)
    }

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

    @GetMapping("/student/{projectName}/stages")
    fun getStudentStagesList(@PathVariable("projectName") projectName: String): StudentStagesResponse {
        return StudentStagesResponse(studentService.getStudentStages(projectName))
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

    @GetMapping("/student/{projectName}/integrations")
    fun getStudentIntegrationsList(@PathVariable("projectName") projectName: String): StudentIntegrationsResponse {
        return StudentIntegrationsResponse(studentService.getStudentIntegrations(projectName))
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

    fun createFileResponse(file: File): ResponseEntity<*> {
        val headers = HttpHeaders()
        headers.add("X-Suggested-Filename", file.name);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.name + "\"")
        headers.add("Access-Control-Expose-Headers", HttpHeaders.CONTENT_DISPOSITION + "," + HttpHeaders.CONTENT_LENGTH)

        return ResponseEntity.ok().headers(headers).body<Any>(file.readBytes())
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

    @GetMapping("/student/integration/{projectName}/{integrationName}/{testCaseName}/logs")
    fun downloadStudentIntegrationLogsFile(
            @PathVariable("projectName") projectName: String,
            @PathVariable("integrationName") integrationName: String,
            @PathVariable("testCaseName") testCaseName: String): ResponseEntity<*> {
        return createFileResponse(studentService.getIntegrationLogsFile(projectName, integrationName, testCaseName))
    }
}

class ProjectResponse(val projects: List<String>)
class StagesResponse(val projectDescription: String?, val projectEnvironment: String?, val stages: List<StageDao>)
class StageDao(val stageName: String, val stageDescription: String?, val startDate: String?, val endDate: String?, val pointsNumber: String?, val testCases: List<TestCase>)
class TestCase(val testCaseName: String, val parameters: String?, val inputFileName: String?, val outputFileName: String?)
class StudentStagesResponse(val stages: List<StudentStage>)
class StudentStage(val stageName: String, val binaryName: String?, val reportName: String?, val testCases: List<TestCaseWithResult>, val passedTestCasesCount: Int, val allTestCasesCount: Int, val startDate: String?, val endDate: String?, val pointsNumber: String?, val totalPointsNumber: String?, val codeLink: String?, val enable: Boolean)
class StudentIntegrationsResponse(val integrations: List<StudentIntegration>)
class StudentIntegration(val integrationName: String, val integrationStages: List<IntegrationStageDao>, val testCases: List<TestCaseWithResult>, val passedTestCasesCount: Int, val allTestCasesCount: Int, val enable: Boolean)
class TestCaseWithResult(val testCaseName: String, val parameters: String?, val status: String = "NO RUN", val message: String?, val isLogsFile: Boolean = false)
class GroupsResponse(val groups: List<Group>)
class Group(val groupName: String, val projectName: String, val students: List<String>)
data class GroupsDao(val groups: List<GroupDao> = ArrayList())
data class GroupDao(val name: String = "", val students: List<String> = ArrayList())
data class IntegrationsDao(val integrations: List<IntegrationDao> = ArrayList())
data class IntegrationDao(val name: String, val integrationStages: List<IntegrationStageDao>, val testCases: List<TestCase>?)
data class IntegrationStageDao(val name: String, val orderNumber: Int, val stageName: String)
