package com.drabarz.karolina.testplatformrunner.service

import com.drabarz.karolina.testplatformrunner.api.StudentIntegration
import com.drabarz.karolina.testplatformrunner.api.StudentStage
import com.drabarz.karolina.testplatformrunner.api.TestCaseWithResult
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
        val jarService: JarService) {

    private final val stagePathProvider = StagePathProvider()
    private final val integrationPathProvider = IntegrationPathProvider()

    fun runStageTests(projectName: String, stageName: String): List<TestResponse> {
        val testResponses = jarService.runJar(projectName, stageName)
        saveTestResponses(stagePathProvider.getStudentResultsDir(projectName, stageName), testResponses)

        return testResponses
    }

    fun runIntegrationTests(projectName: String, integrationName: String): List<TestResponse> {
        val testResponses = jarService.runJars(projectName, integrationName)
        saveTestResponses(integrationPathProvider.getStudentResultsDir(projectName, integrationName), testResponses)

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

    fun saveFile(projectName: String, stageName: String, uploadedFile: MultipartFile, fileType: FileType) {
        var dir = stagePathProvider.getStudentTaskDir(projectName, stageName)
        if (fileType == FileType.BINARY) {
            dir = stagePathProvider.getStudentBinDir(projectName, stageName)
        } else {
            dir = stagePathProvider.getStudentReportDir(projectName, stageName)
        }

        dir.mkdirs()

        if (dir.list().isNotEmpty()) {
            dir.listFiles().first().delete()
        }

        val outputFile = File(dir.path, uploadedFile.originalFilename)
        uploadedFile.transferTo(outputFile)
    }

    fun getStudentStages(projectName: String): List<StudentStage> {
        return stageService
                .getStages(projectName)
                .filter { !it.startDate.isNullOrBlank() }
                .map { stage ->
                    StudentStage(
                            stage.stageName,
                            getBinaryName(projectName, stage.stageName),
                            getReportName(projectName, stage.stageName),
                            getTestCasesWithResults(projectName, stage.stageName, stage.testCases.map { StudentTestCase(it.testCaseName, it.parameters) }).sortedBy { it.testCaseName },
                            getTestCasesWithResults(projectName, stage.stageName, stage.testCases.map { StudentTestCase(it.testCaseName, it.parameters) }).count { it.status == "SUCCESS" },
                            stage.testCases.size,
                            stage.startDate,
                            stage.endDate,
                            "0",
                            stage.pointsNumber,
                            getCodeLink(projectName, stage.stageName),
                            isEnable(stage.endDate))
                }.sortedBy { it.endDate }
    }

    private fun isEnable(endDate: String?): Boolean {
        return endDate.isNullOrBlank() || endDate.toDate()!!.after(Date())
    }

    private fun getCodeLink(projectName: String, stageName: String): String? {
        val dir = stagePathProvider.getStudentCodeDir(projectName, stageName)

        val codeFile = File(dir.path, PathProvider.CODE)

        if (!codeFile.exists() || codeFile.readText().isBlank()) {
            return null
        }

        return codeFile.readText()
    }

    private fun getBinaryName(projectName: String, stageName: String): String? {
        return getStageFileName(projectName, stageName, FileType.BINARY)
    }

    private fun getReportName(projectName: String, stageName: String): String? {
        return getStageFileName(projectName, stageName, FileType.REPORT)
    }

    private fun getStageFileName(projectName: String, stageName: String, fileType: FileType): String? {

        val stageDir = stagePathProvider.getStudentTaskDir(projectName, stageName)
        if (!stageDir.exists()) {
            return null
        }

        val fileDir: File
        if (fileType == FileType.BINARY) {
            fileDir = stagePathProvider.getStudentBinDir(projectName, stageName)
        } else {
            fileDir = stagePathProvider.getStudentReportDir(projectName, stageName)
        }

        if (!fileDir.exists() || fileDir.list().size != 1) {
            return null
        }

        return fileDir.list().first()
    }

    private fun getTestCasesWithResults(projectName: String, stageName: String, testCases: List<StudentTestCase>): List<TestCaseWithResult> {
        val resultFile = File(stagePathProvider.getStudentResultsDir(projectName, stageName), "result.json")

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
                    isLogsFileExist(projectName, stageName, testCase.name))
        }
    }

    private fun isLogsFileExist(projectName: String, stageName: String, testCase: String): Boolean {
        return stagePathProvider.getStudentLogsFileDir(projectName, stageName, testCase).exists()
    }

    fun saveCodeLink(projectName: String, stageName: String, codeLink: String): String {
        val dir = stagePathProvider.getStudentCodeDir(projectName, stageName)
        dir.mkdirs()

        val outputFile = File(dir.path, PathProvider.CODE)
        outputFile.writeText(codeLink)

        return "200"
    }

    fun getJar(projectName: String, stageName: String): File {
        return getSingleFile(stagePathProvider.getStudentBinDir(projectName, stageName))
    }

    fun getReport(projectName: String, stageName: String): File {
        return getSingleFile(stagePathProvider.getStudentReportDir(projectName, stageName))
    }

    fun getStageLogsFile(projectName: String, stageName: String, testCaseName: String): File {
        return getExactFile(stagePathProvider.getStudentLogsFileDir(projectName, stageName, testCaseName))
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

    fun getStudentIntegrations(projectName: String): List<StudentIntegration> {
        return integrationService
                .getIntegrations(projectName)
                .integrations
                .map { integration ->
                    StudentIntegration(
                            integration.name,
                            integration.integrationStages,
                            getTestCasesWithResultsIntegration(projectName, integration.name, integration.testCases!!.map { StudentTestCase(it.testCaseName, it.parameters) }),
                            getTestCasesWithResultsIntegration(projectName, integration.name, integration.testCases.map { StudentTestCase(it.testCaseName, it.parameters) }).count { it.status == "SUCCESS" },
                            integration.testCases.count(),
                            true
                    )
                }.sortedBy { it.integrationName }
    }

    private fun getTestCasesWithResultsIntegration(projectName: String, integrationName: String, testCases: List<StudentTestCase>): List<TestCaseWithResult> {
        val resultFile = File(integrationPathProvider.getStudentResultsDir(projectName, integrationName), "result.json")

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
                    isLogsFileExistIntegration(projectName, integrationName, testCase.name))
        }
    }

    private fun isLogsFileExistIntegration(projectName: String, integrationName: String, testCase: String): Boolean {
        return integrationPathProvider.getStudentLogsFileDir(projectName, integrationName, testCase).exists()
    }

    fun getIntegrationLogsFile(projectName: String, integrationName: String, testCaseName: String): File {
        return getExactFile(integrationPathProvider.getStudentLogsFileDir(projectName, integrationName, testCaseName))
    }

    fun getStudentProjects(userName: String?): List<String> {
        if (userName == null) {
            throw IllegalAccessError()
        }

        return projectService.getStudentsProjects(userName)
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