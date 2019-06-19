package com.drabarz.karolina.testplatformrunner

import com.drabarz.karolina.testplatformrunner.model.ProjectsRepository
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

@SpringBootApplication
class TestPlatformRunnerApplication

fun main(args: Array<String>) {
    TestPlatformRunnerApplication::class.java.getResource("/static/Dockerfile").readText().let { print(it) }
    runApplication<TestPlatformRunnerApplication>(*args)
}

@CrossOrigin(origins = ["http://localhost:3000", "http://192.168.0.80:3000"], allowCredentials = "true")
@RestController
class TestPlatformApi(val studentService: StudentService,
                      val testCaseService: TestCaseService,
                      val stagesService: StageService,
                      val projectService: ProjectService,
                      val projectsRepository: ProjectsRepository,
                      val groupService: GroupService,
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

    @GetMapping("/student/{projectName}/{stageName}/{testCaseName}/logs")
    fun downloadStudentLogsFile(
            @PathVariable("projectName") projectName: String,
            @PathVariable("stageName") stageName: String,
            @PathVariable("testCaseName") testCaseName: String): ResponseEntity<*> {
        return createFileResponse(studentService.getLogsFile(projectName, stageName, testCaseName))
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

    @GetMapping("/{projectName}/description")
    @ResponseBody
    fun downloadProjectDescriptionFile(@PathVariable("projectName") projectName: String): ResponseEntity<*> {
        return createFileResponse(projectService.getProjectDescription(projectName))
    }

    @GetMapping("/{projectName}/{stageName}/description")
    @ResponseBody
    fun downloadStageDescriptionFile(
            @PathVariable("projectName") projectName: String,
            @PathVariable("stageName") stageName: String): ResponseEntity<*> {
        return createFileResponse(stagesService.getStageDescription(projectName, stageName))
    }

    @PostMapping("/stage/description")
    fun addStageDescription(
            @RequestParam("file") uploadedFile: MultipartFile,
            @RequestParam("projectName") projectName: String,
            @RequestParam("stageName") stageName: String): String {
        return stagesService.addStageDescription(uploadedFile, projectName, stageName)
    }

    @GetMapping("/{projectName}/stages")
    fun getStagesList(@PathVariable("projectName") projectName: String): StagesResponse {
        return StagesResponse(projectService.getProjectDescriptionName(projectName), stagesService.getStages(projectName))
    }

    @GetMapping("/student/{projectName}/stages")
    fun getStudentStagesList(@PathVariable("projectName") projectName: String): StudentStagesResponse {
        return StudentStagesResponse(studentService.getStudentStages(projectName))
    }

    @PostMapping("/stage")
    fun addStage(@RequestParam("projectName") projectName: String, @RequestParam("stageName") stageName: String): String {
        return stagesService.addStage(projectName, stageName)
    }

    @DeleteMapping("/{projectName}/{stageName}")
    fun deleteStage(@PathVariable("projectName") projectName: String,
                    @PathVariable("stageName") stageName: String): String {
        return stagesService.deleteStage(projectName, stageName)
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

    @GetMapping("/{projectName}/{stageName}/{testCaseName}/{fileType}")
    @ResponseBody
    fun downloadTestCaseFile(
            @PathVariable("projectName") projectName: String,
            @PathVariable("stageName") stageName: String,
            @PathVariable("testCaseName") testCaseName: String,
            @PathVariable("fileType") fileType: String): ResponseEntity<*> {
        return createFileResponse(testCaseService.getTestCaseFile(projectName, stageName, testCaseName, fileType))
    }

    @PostMapping("/{projectName}/{stageName}/{testCaseName}/{fileType}")
    fun uploadTestCaseFile(
            @PathVariable("projectName") projectName: String,
            @PathVariable("stageName") stageName: String,
            @PathVariable("testCaseName") testCaseName: String,
            @PathVariable("fileType") fileType: String,
            @RequestParam("file") file: MultipartFile): String {
        return testCaseService.uploadTestCaseFile(projectName, stageName, testCaseName, fileType, file)
    }

    @DeleteMapping("/{projectName}/{stageName}/{testCaseName}")
    fun deleteTestCase(
            @PathVariable("projectName") projectName: String,
            @PathVariable("stageName") stageName: String,
            @PathVariable("testCaseName") testCaseName: String): String {
        return testCaseService.deleteTestCase(projectName, stageName, testCaseName)
    }

    fun createFileResponse(file: File): ResponseEntity<*> {
        val headers = HttpHeaders()
        headers.add("X-Suggested-Filename", file.name);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.name + "\"")
        headers.add("Access-Control-Expose-Headers", HttpHeaders.CONTENT_DISPOSITION + "," + HttpHeaders.CONTENT_LENGTH)

        return ResponseEntity.ok().headers(headers).body<Any>(file.readBytes())
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
}

class ProjectResponse(val projects: List<String>)
class StagesResponse(val projectDescription: String?, val stages: List<StageDao>)
class StageDao(val stageName: String, val stageDescription: String?, val testCases: List<TestCase>)
class TestCase(val testCaseName: String, val inputFileName: String?, val outputFileName: String?)
class StudentStagesResponse(val stages: List<StudentStage>)
class StudentStage(val stageName: String, val binaryName: String?, val reportName: String?, val testCases: List<TestCaseWithResult>, val passedTestCasesCount: Int, val allTestCasesCount: Int, val deadline: String, val codeLink: String?)
class TestCaseWithResult(val testCaseName: String, val status: String = "NO RUN", val message: String?, val isLogsFile: Boolean = false)
class GroupsResponse(val groups: List<Group>)
class Group(val groupName: String, val projectName: String, val students: List<String>)
data class GroupsDao(val groups: List<GroupDao> = ArrayList())
data class GroupDao(val name: String = "", val students: List<String> = ArrayList())
