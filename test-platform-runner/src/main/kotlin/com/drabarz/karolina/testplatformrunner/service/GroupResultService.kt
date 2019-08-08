package com.drabarz.karolina.testplatformrunner.service

import com.drabarz.karolina.testplatformrunner.api.*
import com.drabarz.karolina.testplatformrunner.service.helper.IntegrationPathProvider
import com.drabarz.karolina.testplatformrunner.service.helper.PathProvider
import com.drabarz.karolina.testplatformrunner.service.helper.StagePathProvider
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

@Component
class GroupResultService(
        val stageService: StageService,
        val integrationService: IntegrationService,
        val groupService: GroupService,
        val jarService: JarService) {

    private final val stagePathProvider = StagePathProvider()
    private final val integrationPathProvider = IntegrationPathProvider()

    fun runStageTests(userName: String, groupName: String, projectName: String, stageName: String): List<TestResponse> {
        val testResponses = jarService.runJar(groupName, projectName, stageName)
        saveTestResponses(stagePathProvider.getStudentResultsDir(groupName, projectName, stageName), userName, testResponses)

        return testResponses
    }

    fun runIntegrationTests(userName: String, groupName: String, projectName: String, integrationName: String): List<TestResponse> {
        val testResponses = jarService.runJars(groupName, projectName, integrationName)
        saveTestResponses(integrationPathProvider.getStudentResultsDir(groupName, projectName, integrationName), userName, testResponses)

        return testResponses
    }

    private fun saveTestResponses(resultsDir: File, userName: String, testResponses: List<TestResponse>) {
        resultsDir.mkdirs()

        val fullTestResponse = FullTestResponse(Date().time, userName, testResponses)

        val file = File(resultsDir, "result.json")

        val out = ByteArrayOutputStream()
        val mapper = ObjectMapper()

        var fullTestResponses = mutableListOf<FullTestResponse>()

        if (file.exists()) {
            val jsonData = file.readBytes()
            fullTestResponses = jacksonObjectMapper().readerFor(Array<FullTestResponse>::class.java).readValue<Array<FullTestResponse>>(jsonData).toMutableList()
        }

        fullTestResponses.add(fullTestResponse)

        mapper.writeValue(out, fullTestResponses)

        file.writeBytes(out.toByteArray())
    }

    fun saveFile(userName: String, groupName: String, projectName: String, stageName: String, uploadedFile: MultipartFile, fileType: FileType) {
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

    fun getStudentStages(groupName: String, projectName: String): List<StudentStage> {
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
                            countSuccessfulStageGroups(projectName, stage.stageName),
                            countTotalGroups(projectName),
                            getCodeLink(groupName, projectName, stage.stageName),
                            isEnable(stage.endDate))
                }.sortedBy { it.endDate }
    }

    private fun countSuccessfulStageGroups(projectName: String, stageName: String): Int {
        var counter = 0
        groupService.getGroups(projectName).groups.forEach {
            val resultDir = stagePathProvider.getStudentResultsDir(it.groupName, projectName, stageName)
            if (resultDir.exists() && resultDir.list().size == 1) {
                val jsonData = resultDir.listFiles().first().readBytes()
                val fullResults = jacksonObjectMapper().readerFor(Array<FullTestResponse>::class.java).readValue<Array<FullTestResponse>>(jsonData).toList()

                val results = fullResults.sortedBy { it.date }.last().testResponses

                if (results.all { it.status == "SUCCESS" }) {
                    counter++
                }
            }
        }

        return counter
    }

    fun getStudentPreviewStages(groupName: String, projectName: String): List<StudentPreviewStage> {
        return stageService
                .getStages(projectName)
                .filter { !it.startDate.isNullOrBlank() }
                .map { stage ->
                    StudentPreviewStage(
                            stage.stageName,
                            getBinaryName(groupName, projectName, stage.stageName),
                            getReportName(groupName, projectName, stage.stageName),
                            getTestCasesWithResults(groupName, projectName, stage.stageName, stage.testCases.map { StudentTestCase(it.testCaseName, it.parameters) }).sortedBy { it.testCaseName },
                            getTestCasesWithResults(groupName, projectName, stage.stageName, stage.testCases.map { StudentTestCase(it.testCaseName, it.parameters) }).count { it.status == "SUCCESS" },
                            stage.testCases.size,
                            stage.startDate,
                            stage.endDate,
                            countSuccessfulStageGroups(projectName, stage.stageName),
                            countTotalGroups(projectName),
                            getCodeLink(groupName, projectName, stage.stageName),
                            hasStageStatistics(groupName, projectName, stage.stageName))
                }.sortedBy { it.endDate }
    }

    private fun countTotalGroups(projectName: String): Int {
        return groupService.getGroups(projectName).groups.count()
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
        val fullResults = jacksonObjectMapper().readerFor(Array<FullTestResponse>::class.java).readValue<Array<FullTestResponse>>(jsonData).toList()

        val results = fullResults.sortedBy { it.date }.last().testResponses

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

    fun saveCodeLink(userName: String, groupName: String, projectName: String, stageName: String, codeLink: String): String {
        val dir = stagePathProvider.getStudentCodeDir(groupName, projectName, stageName)
        dir.mkdirs()

        val outputFile = File(dir.path, PathProvider.CODE)
        outputFile.writeText(codeLink)

        return "200"
    }

    fun getJar(groupName: String, projectName: String, stageName: String): File {
        return getSingleFile(stagePathProvider.getStudentBinDir(groupName, projectName, stageName))
    }

    fun getReport(groupName: String, projectName: String, stageName: String): File {
        return getSingleFile(stagePathProvider.getStudentReportDir(groupName, projectName, stageName))
    }

    fun getStageLogsFile(groupName: String, projectName: String, stageName: String, testCaseName: String): File {
        return getExactFile(stagePathProvider.getStudentLogsFileDir(groupName, projectName, stageName, testCaseName))
    }

    private fun getSingleFile(dir: File): File {
        if (!dir.exists() && dir.list().size != 1) {
            throw java.lang.RuntimeException("Error file doesn't exist")
        }

        return dir.listFiles().first()
    }

    private fun getExactFile(file: File): File {
        if (!file.exists()) {
            throw java.lang.RuntimeException("Error file doesn't exist")
        }

        return file
    }

    fun getStudentIntegrations(groupName: String, projectName: String): List<StudentIntegration> {
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
                            countSuccessfulIntegrationGroups(projectName, integration.name),
                            countTotalGroups(projectName),
                            true
                    )
                }.sortedBy { it.integrationName }
    }

    private fun countSuccessfulIntegrationGroups(projectName: String, integrationName: String): Int {
        var counter = 0
        groupService.getGroups(projectName).groups.forEach {
            val resultDir = integrationPathProvider.getStudentResultsDir(it.groupName, projectName, integrationName)
            if (resultDir.exists() && resultDir.list().size == 1) {
                val jsonData = resultDir.listFiles().first().readBytes()
                val fullResults = jacksonObjectMapper().readerFor(Array<FullTestResponse>::class.java).readValue<Array<FullTestResponse>>(jsonData).toList()

                val results = fullResults.sortedBy { it.date }.last().testResponses

                if (results.all { it.status == "SUCCESS" }) {
                    counter++
                }
            }
        }

        return counter
    }

    fun getStudentPreviewIntegrations(groupName: String, projectName: String): List<StudentPreviewIntegration> {
        return integrationService
                .getIntegrations(projectName)
                .integrations
                .map { integration ->
                    StudentPreviewIntegration(
                            integration.name,
                            integration.integrationStages,
                            getTestCasesWithResultsIntegration(groupName, projectName, integration.name, integration.testCases!!.map { StudentTestCase(it.testCaseName, it.parameters) }),
                            getTestCasesWithResultsIntegration(groupName, projectName, integration.name, integration.testCases.map { StudentTestCase(it.testCaseName, it.parameters) }).count { it.status == "SUCCESS" },
                            integration.testCases.count(),
                            countSuccessfulIntegrationGroups(projectName, integration.name),
                            countTotalGroups(projectName),
                            hasStatistics(integrationPathProvider.getStudentResultsDir(groupName, projectName, integration.name))
                    )
                }.sortedBy { it.integrationName }
    }

    private fun getTestCasesWithResultsIntegration(groupName: String, projectName: String, integrationName: String, testCases: List<StudentTestCase>): List<TestCaseWithResult> {
        val resultFile = File(integrationPathProvider.getStudentResultsDir(groupName, projectName, integrationName), "result.json")

        if (!resultFile.exists()) {
            return testCases.map { testCase -> TestCaseWithResult(testCase.name, testCase.parameters, "NO RUN", null) }
        }

        val jsonData = resultFile.readBytes()
        val fullResults = jacksonObjectMapper().readerFor(Array<FullTestResponse>::class.java).readValue<Array<FullTestResponse>>(jsonData).toList()

        val results = fullResults.sortedBy { it.date }.last().testResponses

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

    fun getIntegrationLogsFile(groupName: String, projectName: String, integrationName: String, testCaseName: String): File {
        return getExactFile(integrationPathProvider.getStudentLogsFileDir(groupName, projectName, integrationName, testCaseName))
    }

    fun getStageStatisticsFile(groupName: String, projectName: String, stageName: String): File {
        return getResultsFile(stagePathProvider.getStudentResultsDir(groupName, projectName, stageName))
    }

    fun getIntegrationStatisticsFile(groupName: String, projectName: String, integrationName: String): File {
        return getResultsFile(integrationPathProvider.getStudentResultsDir(groupName, projectName, integrationName))
    }

    fun getResultsFile(resultsDir: File): File {
        if (hasStatistics(resultsDir)) {
            return resultsDir.listFiles().first()
        }

        throw NoSuchFileException(resultsDir)
    }

    private fun hasStageStatistics(groupName: String, projectName: String, stageName: String): Boolean {
        return hasStatistics(stagePathProvider.getStudentResultsDir(groupName, projectName, stageName))
    }

    private fun hasIntegrationStatistics(groupName: String, projectName: String, stageName: String): Boolean {
        return hasStatistics(integrationPathProvider.getStudentResultsDir(groupName, projectName, stageName))
    }

    private fun hasStatistics(resultsDir: File): Boolean {
        return resultsDir.exists() && resultsDir.list().size == 1
    }
}

data class FullTestResponse(val date: Long, val userName: String, val testResponses: List<TestResponse>)

enum class FileType {
    BINARY,
    REPORT,
    RESULTS
}

data class StudentTestCase(val name: String, val parameters: String?)