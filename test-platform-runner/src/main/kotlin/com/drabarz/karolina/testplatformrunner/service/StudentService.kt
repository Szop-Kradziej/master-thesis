package com.drabarz.karolina.testplatformrunner.service

import com.drabarz.karolina.testplatformrunner.api.StudentIntegration
import com.drabarz.karolina.testplatformrunner.api.StudentStage
import com.drabarz.karolina.testplatformrunner.api.TestCaseWithResult
import com.drabarz.karolina.testplatformrunner.model.GroupsRepository
import com.drabarz.karolina.testplatformrunner.service.helper.IntegrationPathProvider
import com.drabarz.karolina.testplatformrunner.service.helper.PathProvider
import com.drabarz.karolina.testplatformrunner.service.helper.StagePathProvider
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

@Component
class StudentService(
        val stageService: StageService,
        val integrationService: IntegrationService,
        val projectService: ProjectService,
        val groupsRepository: GroupsRepository,
        val jarService: JarService) {

    private final val stagePathProvider = StagePathProvider()
    private final val integrationPathProvider = IntegrationPathProvider()

    fun runStageTests(studentName: String?, projectName: String, stageName: String): List<TestResponse> {
        val groupName = getGroupName(studentName, projectName)
        val testResponses = jarService.runJar(groupName, projectName, stageName)
        saveTestResponses(stagePathProvider.getStudentResultsDir(groupName, projectName, stageName), testResponses)

        return testResponses
    }

    private fun getGroupName(studentName: String?, projectName: String): String {
        if (studentName == null) {
            throw IllegalAccessError()
        }

        println("student name: " + studentName + " projectName: " + projectName)

        val group = groupsRepository.findAllByStudents_Name(studentName)
                .filter { it.project.name == projectName }

        return group.first().name
    }

    fun runIntegrationTests(studentName: String?, projectName: String, integrationName: String): List<TestResponse> {
        val groupName = getGroupName(studentName, projectName)
        val testResponses = jarService.runJars(groupName, projectName, integrationName)
        saveTestResponses(integrationPathProvider.getStudentResultsDir(groupName, projectName, integrationName), testResponses)

        return testResponses
    }

    private fun saveTestResponses(resultsDir: File, testResponses: List<TestResponse>) {
        resultsDir.mkdirs()

        val file = File(resultsDir, "result.json");

        val out = ByteArrayOutputStream()
        val mapper = ObjectMapper()

        mapper.writeValue(out, testResponses)

        file.writeBytes(out.toByteArray())
    }

    fun saveFile(studentName: String?, projectName: String, stageName: String, uploadedFile: MultipartFile, fileType: FileType) {
        val groupName = getGroupName(studentName, projectName)
        var dir = stagePathProvider.getStudentTaskDir(groupName, projectName, stageName)
        if (fileType == FileType.BINARY) {
            dir = stagePathProvider.getStudentBinDir(groupName, projectName, stageName)
        } else {
            dir = stagePathProvider.getStudentReportDir(groupName, projectName, stageName)
        }

        dir.mkdirs()

        if (dir.list().isNotEmpty()) {
            dir.listFiles().first().delete()
        }

        val outputFile = File(dir.path, uploadedFile.originalFilename)
        uploadedFile.transferTo(outputFile)
    }

    fun getStudentStages(studentName: String?, projectName: String): List<StudentStage> {
        val groupName = getGroupName(studentName, projectName)
        return stageService
                .getStages(projectName)
                .filter { !it.startDate.isNullOrBlank() }
                .map { stage ->
                    StudentStage(
                            stage.stageName,
                            getBinaryName(groupName, projectName, stage.stageName),
                            getReportName(groupName, projectName, stage.stageName),
                            getTestCasesWithResults(groupName, projectName, stage.stageName, stage.testCases.map { StudentTestCase(it.testCaseName, it.parameters) }).sortedBy { it.testCaseName },
                            getTestCasesWithResults(groupName, projectName, stage.stageName, stage.testCases.map { StudentTestCase(it.testCaseName, it.parameters) }).count { it.status == "SUCCESS" },
                            stage.testCases.size,
                            stage.startDate,
                            stage.endDate,
                            "0",
                            stage.pointsNumber,
                            getCodeLink(groupName, projectName, stage.stageName),
                            isEnable(stage.endDate))
                }.sortedBy { it.endDate }
    }

    private fun isEnable(endDate: String?): Boolean {
        return endDate.isNullOrBlank() || endDate.toDate()!!.after(Date())
    }

    private fun getCodeLink(groupName: String, projectName: String, stageName: String): String? {
        val dir = stagePathProvider.getStudentCodeDir(groupName, projectName, stageName)

        val codeFile = File(dir.path, PathProvider.CODE)

        if (!codeFile.exists() || codeFile.readText().isBlank()) {
            return null
        }

        return codeFile.readText()
    }

    private fun getBinaryName(groupName: String, projectName: String, stageName: String): String? {
        return getStageFileName(groupName, projectName, stageName, FileType.BINARY)
    }

    private fun getReportName(groupName: String, projectName: String, stageName: String): String? {
        return getStageFileName(groupName, projectName, stageName, FileType.REPORT)
    }

    private fun getStageFileName(groupName: String, projectName: String, stageName: String, fileType: FileType): String? {
        val stageDir = stagePathProvider.getStudentTaskDir(groupName, projectName, stageName)
        if (!stageDir.exists()) {
            return null
        }

        val fileDir: File
        if (fileType == FileType.BINARY) {
            fileDir = stagePathProvider.getStudentBinDir(groupName, projectName, stageName)
        } else {
            fileDir = stagePathProvider.getStudentReportDir(groupName, projectName, stageName)
        }

        if (!fileDir.exists() || fileDir.list().size != 1) {
            return null
        }

        return fileDir.list().first()
    }

    private fun getTestCasesWithResults(groupName: String, projectName: String, stageName: String, testCases: List<StudentTestCase>): List<TestCaseWithResult> {
        val resultFile = File(stagePathProvider.getStudentResultsDir(groupName, projectName, stageName), "result.json")

        if (!resultFile.exists()) {
            return testCases.map { testCase -> TestCaseWithResult(testCase.name, testCase.parameters, "NO RUN", null) }
        }

        val jsonData = resultFile.readBytes()
        val results = jacksonObjectMapper().readerFor(Array<TestResponse>::class.java).readValue<Array<TestResponse>>(jsonData).toList()

        return testCases.map { testCase ->
            TestCaseWithResult(
                    testCase.name,
                    testCase.parameters,
                    results.find { it.testCaseName == testCase.name }?.status ?: "NO RUN",
                    results.find { it.testCaseName == testCase.name }?.message,
                    isLogsFileExist(groupName, projectName, stageName, testCase.name))
        }
    }

    private fun isLogsFileExist(groupName: String, projectName: String, stageName: String, testCase: String): Boolean {
        return stagePathProvider.getStudentLogsFileDir(groupName, projectName, stageName, testCase).exists()
    }

    fun saveCodeLink(studentName: String?, projectName: String, stageName: String, codeLink: String): String {
        val groupName = getGroupName(studentName, projectName)
        val dir = stagePathProvider.getStudentCodeDir(groupName, projectName, stageName)
        dir.mkdirs()

        val outputFile = File(dir.path, PathProvider.CODE)
        outputFile.writeText(codeLink)

        return "200"
    }

    fun getJar(studentName: String?, projectName: String, stageName: String): File {
        val groupName = getGroupName(studentName, projectName)
        return getSingleFile(stagePathProvider.getStudentBinDir(groupName, projectName, stageName))
    }

    fun getReport(studentName: String?, projectName: String, stageName: String): File {
        val groupName = getGroupName(studentName, projectName)
        return getSingleFile(stagePathProvider.getStudentReportDir(groupName, projectName, stageName))
    }

    fun getStageLogsFile(studentName: String?, projectName: String, stageName: String, testCaseName: String): File {
        val groupName = getGroupName(studentName, projectName)
        return getExactFile(stagePathProvider.getStudentLogsFileDir(groupName, projectName, stageName, testCaseName))
    }

    fun getSingleFile(dir: File): File {
        if (!dir.exists() && dir.list().size != 1) {
            throw java.lang.RuntimeException("Error file doesn't exist")
        }

        return dir.listFiles().first()
    }

    fun getExactFile(file: File): File {
        if (!file.exists()) {
            throw java.lang.RuntimeException("Error file doesn't exist")
        }

        return file
    }

    fun getStudentIntegrations(studentName: String?, projectName: String): List<StudentIntegration> {
        val groupName = getGroupName(studentName, projectName)
        return integrationService
                .getIntegrations(projectName)
                .integrations
                .map { integration ->
                    StudentIntegration(
                            integration.name,
                            integration.integrationStages,
                            getTestCasesWithResultsIntegration(groupName, projectName, integration.name, integration.testCases!!.map { StudentTestCase(it.testCaseName, it.parameters) }),
                            getTestCasesWithResultsIntegration(groupName, projectName, integration.name, integration.testCases.map { StudentTestCase(it.testCaseName, it.parameters) }).count { it.status == "SUCCESS" },
                            integration.testCases.count(),
                            true
                    )
                }.sortedBy { it.integrationName }
    }

    private fun getTestCasesWithResultsIntegration(groupName: String, projectName: String, integrationName: String, testCases: List<StudentTestCase>): List<TestCaseWithResult> {
        val resultFile = File(integrationPathProvider.getStudentResultsDir(groupName, projectName, integrationName), "result.json")

        if (!resultFile.exists()) {
            return testCases.map { testCase -> TestCaseWithResult(testCase.name, testCase.parameters, "NO RUN", null) }
        }

        val jsonData = resultFile.readBytes()
        val results = jacksonObjectMapper().readerFor(Array<TestResponse>::class.java).readValue<Array<TestResponse>>(jsonData).toList()

        return testCases.map { testCase ->
            TestCaseWithResult(
                    testCase.name,
                    testCase.parameters,
                    results.find { it.testCaseName == testCase.name }?.status ?: "NO RUN",
                    results.find { it.testCaseName == testCase.name }?.message,
                    isLogsFileExistIntegration(groupName, projectName, integrationName, testCase.name))
        }
    }

    private fun isLogsFileExistIntegration(groupName: String, projectName: String, integrationName: String, testCase: String): Boolean {
        return integrationPathProvider.getStudentLogsFileDir(groupName, projectName, integrationName, testCase).exists()
    }

    fun getIntegrationLogsFile(studentName: String?, projectName: String, integrationName: String, testCaseName: String): File {
        val groupName = getGroupName(studentName, projectName)
        return getExactFile(integrationPathProvider.getStudentLogsFileDir(groupName, projectName, integrationName, testCaseName))
    }

    fun getStudentProjects(studentName: String?): List<String> {
        if (studentName == null) {
            throw IllegalAccessError()
        }

        return projectService.getStudentsProjects(studentName)
    }

    companion object {
        val log = LoggerFactory.getLogger(StudentService::class.java)
    }
}

enum class FileType {
    BINARY,
    REPORT,
    RESULTS
}

data class StudentTestCase(val name:String, val parameters: String?)