package com.drabarz.karolina.testplatformrunner.service

import com.drabarz.karolina.testplatformrunner.api.*
import com.drabarz.karolina.testplatformrunner.service.helper.IntegrationPathProvider
import com.drabarz.karolina.testplatformrunner.service.helper.PathProvider
import com.drabarz.karolina.testplatformrunner.service.helper.StagePathProvider
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.opencsv.CSVWriter
import org.hibernate.annotations.common.util.impl.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Files
import java.util.*

@Component
class GroupResultService(
        val stageService: StageService,
        val integrationService: IntegrationService,
        val groupService: GroupService,
        val binService: BinService) {

    private final val stagePathProvider = StagePathProvider()
    private final val integrationPathProvider = IntegrationPathProvider()

    fun runStageTests(userName: String, groupName: String, projectName: String, stageName: String): List<TestResponse> {
        val testResponses = binService.runBin(groupName, projectName, stageName)
        saveTestResponses(stagePathProvider.getStudentResultsDir(groupName, projectName, stageName), userName, testResponses)

        return testResponses
    }

    fun runIntegrationTests(userName: String, groupName: String, projectName: String, integrationName: String): List<TestResponse> {
        val testResponses = binService.runBins(groupName, projectName, integrationName)
        saveTestResponses(integrationPathProvider.getStudentResultsDir(groupName, projectName, integrationName), userName, testResponses)

        return testResponses
    }

    private fun saveTestResponses(resultsDir: File, userName: String, testResponses: List<TestResponse>) {
        resultsDir.mkdirs()

        val fullTestResponse = FullTestResponse(Date().time, userName, testResponses)

        val file = File(resultsDir, RESULT_FILE_NAME)

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
        log.info("Adding $fileType file for group: $groupName for stage: $stageName in project: $projectName")

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

        log.info("File $fileType for group: $groupName for stage: $stageName in project: $projectName added")
    }

    fun getStudentStages(groupName: String, projectName: String): List<StudentStage> {
        log.info("Getting stages data for group: $groupName in project: $projectName")

        return stageService
                .getStages(projectName)
                .filter { !it.startDate.isNullOrBlank() }
                .map { stage ->
                    StudentStage(
                            stage.stageName,
                            getBinaryName(groupName, projectName, stage.stageName),
                            getReportName(groupName, projectName, stage.stageName),
                            getTestCasesWithResults(groupName, projectName, stage.stageName, stage.testCases.map { StudentTestCase(it.testCaseName, null) }).sortedBy { it.testCaseName },
                            getTestCasesWithResults(groupName, projectName, stage.stageName, stage.testCases.map { StudentTestCase(it.testCaseName, null) }).count { it.status == "SUCCESS" },
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
        log.info("Getting stages data for group: $groupName in project: $projectName")

        return stageService
                .getStages(projectName)
                .filter { !it.startDate.isNullOrBlank() }
                .map { stage ->
                    StudentPreviewStage(
                            stage.stageName,
                            getBinaryName(groupName, projectName, stage.stageName),
                            getReportName(groupName, projectName, stage.stageName),
                            getTestCasesWithResults(groupName, projectName, stage.stageName, stage.testCases.map { StudentTestCase(it.testCaseName, null) }).sortedBy { it.testCaseName },
                            getTestCasesWithResults(groupName, projectName, stage.stageName, stage.testCases.map { StudentTestCase(it.testCaseName, null) }).count { it.status == "SUCCESS" },
                            stage.testCases.size,
                            stage.startDate,
                            stage.endDate,
                            stage.comment,
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
        val resultFile = File(stagePathProvider.getStudentResultsDir(groupName, projectName, stageName), RESULT_FILE_NAME)

        if (!resultFile.exists()) {
            return testCases.map { testCase -> TestCaseWithResult(testCase.name, isParametersFileExist(projectName, stageName, testCase.name), "NO RUN", null) }
        }

        val jsonData = resultFile.readBytes()
        val fullResults = jacksonObjectMapper().readerFor(Array<FullTestResponse>::class.java).readValue<Array<FullTestResponse>>(jsonData).toList()

        val results = fullResults.sortedBy { it.date }.last().testResponses

        return testCases.map { testCase ->
            TestCaseWithResult(
                    testCase.name,
                    isParametersFileExist(projectName, stageName, testCase.name),
                    results.find { it.testCaseName == testCase.name }?.status ?: "NO RUN",
                    results.find { it.testCaseName == testCase.name }?.message,
                    isLogsFileExist(groupName, projectName, stageName, testCase.name))
        }
    }

    private fun isParametersFileExist(projectName: String, stageName: String, testCaseName: String): Boolean {
        return stagePathProvider.getTaskTestCaseParametersDir(projectName, stageName, testCaseName).exists() &&
                stagePathProvider.getTaskTestCaseParametersDir(projectName, stageName, testCaseName).list().size == 1 &&
                stagePathProvider.getTaskTestCaseParametersDir(projectName, stageName, testCaseName).listFiles().first().readText().isNotBlank()
    }

    private fun isLogsFileExist(groupName: String, projectName: String, stageName: String, testCaseName: String): Boolean {
        return stagePathProvider.getStudentLogsFileDir(groupName, projectName, stageName, testCaseName).exists()
    }

    fun saveCodeLink(userName: String, groupName: String, projectName: String, stageName: String, codeLink: String): String {
        log.info("Adding code link by $userName for group: $groupName for stage: $stageName in project: $projectName")

        val dir = stagePathProvider.getStudentCodeDir(groupName, projectName, stageName)
        dir.mkdirs()

        val outputFile = File(dir.path, PathProvider.CODE)
        outputFile.writeText(codeLink)

        log.info("Code link for group: $groupName for stage: $stageName in project: $projectName added by $userName")

        return SUCCESS_RESPONSE
    }

    fun getBin(groupName: String, projectName: String, stageName: String): File {
        log.info("Getting bin for group: $groupName for stage: $stageName in project: $projectName")

        return getSingleFile(stagePathProvider.getStudentBinDir(groupName, projectName, stageName))
    }

    fun getReport(groupName: String, projectName: String, stageName: String): File {
        log.info("Getting report for group: $groupName for stage: $stageName in project: $projectName")

        return getSingleFile(stagePathProvider.getStudentReportDir(groupName, projectName, stageName))
    }

    fun getStageLogsFile(groupName: String, projectName: String, stageName: String, testCaseName: String): File {
        log.info("Getting logs for group: $groupName for testCase: $testCaseName in stage: $stageName in project: $projectName")

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
        log.info("Getting integrations data for group: $groupName in project: $projectName")

        return integrationService
                .getIntegrations(projectName)
                .integrations
                .map { integration ->
                    StudentIntegration(
                            integration.name,
                            integration.integrationStages,
                            getTestCasesWithResultsIntegration(groupName, projectName, integration.name, integration.testCases!!.map { StudentTestCase(it.testCaseName, null) }),
                            getTestCasesWithResultsIntegration(groupName, projectName, integration.name, integration.testCases.map { StudentTestCase(it.testCaseName, null) }).count { it.status == "SUCCESS" },
                            integration.testCases.count(),
                            countSuccessfulIntegrationGroups(projectName, integration.name),
                            countTotalGroups(projectName),
                            isIntegrationEnable(groupName, projectName, integration)
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
        log.info("Getting integrations data for group: $groupName in project: $projectName")

        return integrationService
                .getIntegrations(projectName)
                .integrations
                .map { integration ->
                    StudentPreviewIntegration(
                            integration.name,
                            integration.integrationStages,
                            integration.comment,
                            getTestCasesWithResultsIntegration(groupName, projectName, integration.name, integration.testCases!!.map { StudentTestCase(it.testCaseName, null) }),
                            getTestCasesWithResultsIntegration(groupName, projectName, integration.name, integration.testCases.map { StudentTestCase(it.testCaseName, null) }).count { it.status == "SUCCESS" },
                            integration.testCases.count(),
                            countSuccessfulIntegrationGroups(projectName, integration.name),
                            countTotalGroups(projectName),
                            hasIntegrationStatistics(groupName, projectName, integration.name),
                            isIntegrationEnable(groupName, projectName, integration)
                    )
                }.sortedBy { it.integrationName }
    }

    private fun isIntegrationEnable(groupName: String, projectName: String, integration: IntegrationDao): Boolean {
        return integration.integrationStages.all {hasBinForStage(groupName, projectName, it.stageName)}
    }

    private fun hasBinForStage(groupName: String, projectName: String, stageName: String): Boolean {
        val binDir = stagePathProvider.getStudentBinDir(groupName, projectName, stageName)
        return binDir.exists() && binDir.list().size == 1
    }

    private fun getTestCasesWithResultsIntegration(groupName: String, projectName: String, integrationName: String, testCases: List<StudentTestCase>): List<TestCaseWithResult> {
        val resultFile = File(integrationPathProvider.getStudentResultsDir(groupName, projectName, integrationName), RESULT_FILE_NAME)

        if (!resultFile.exists()) {
            return testCases.map { testCase -> TestCaseWithResult(testCase.name, isParametersFileExistIntegration(projectName, integrationName, testCase.name), "NO RUN", null) }
        }

        val jsonData = resultFile.readBytes()
        val fullResults = jacksonObjectMapper().readerFor(Array<FullTestResponse>::class.java).readValue<Array<FullTestResponse>>(jsonData).toList()

        val results = fullResults.sortedBy { it.date }.last().testResponses

        return testCases.map { testCase ->
            TestCaseWithResult(
                    testCase.name,
                    isParametersFileExistIntegration(projectName, integrationName, testCase.name),
                    results.find { it.testCaseName == testCase.name }?.status ?: "NO RUN",
                    results.find { it.testCaseName == testCase.name }?.message,
                    isLogsFileExistIntegration(groupName, projectName, integrationName, testCase.name))
        }
    }

    private fun isParametersFileExistIntegration(projectName: String, stageName: String, testCaseName: String): Boolean {
        return integrationPathProvider.getTaskTestCaseParametersDir(projectName, stageName, testCaseName).exists() &&
                integrationPathProvider.getTaskTestCaseParametersDir(projectName, stageName, testCaseName).list().size == 1 &&
                integrationPathProvider.getTaskTestCaseParametersDir(projectName, stageName, testCaseName).listFiles().first().readText().isNotBlank()
    }

    private fun isLogsFileExistIntegration(groupName: String, projectName: String, integrationName: String, testCase: String): Boolean {
        return integrationPathProvider.getStudentLogsFileDir(groupName, projectName, integrationName, testCase).exists()
    }

    fun getIntegrationLogsFile(groupName: String, projectName: String, integrationName: String, testCaseName: String): File {
        log.info("Getting logs for group: $groupName for testCase: $testCaseName in integration: $integrationName in project: $projectName")

        return getExactFile(integrationPathProvider.getStudentLogsFileDir(groupName, projectName, integrationName, testCaseName))
    }

    fun getStageStatisticsFile(groupName: String, projectName: String, stageName: String): File {
        log.info("Getting statistics for group: $groupName for stage: $stageName in project: $projectName")

        val statisticsFileName = getStatisticsFileName(projectName, stageName, groupName)
        return getResultsFile(stagePathProvider.getStudentResultsDir(groupName, projectName, stageName), statisticsFileName)
    }

    private fun getStatisticsFileName(projectName: String, taskName: String, groupName: String) =
            projectName + "_" + taskName + "_" + groupName + "_" + "statistics.csv"

    fun getIntegrationStatisticsFile(groupName: String, projectName: String, integrationName: String): File {
        log.info("Getting statistics for group: $groupName for integration: $integrationName in project: $projectName")

        val statisticsFileName = getStatisticsFileName(projectName, integrationName, groupName)
        return getResultsFile(integrationPathProvider.getStudentResultsDir(groupName, projectName, integrationName), statisticsFileName)
    }

    private fun getResultsFile(resultsDir: File, statisticsFileName: String): File {
        if (hasStatistics(resultsDir)) {
            return convertResultFileToCsv(resultsDir, statisticsFileName)
        }

        throw NoSuchFileException(resultsDir)
    }

    private fun convertResultFileToCsv(resultDir: File, statisticsFileName: String): File {
        val jsonData = resultDir.listFiles().find { it.name == RESULT_FILE_NAME }!!.readBytes()
        val fullTestResponses = jacksonObjectMapper().readerFor(Array<FullTestResponse>::class.java).readValue<Array<FullTestResponse>>(jsonData).toMutableList()

        Files.newBufferedWriter(File(resultDir, statisticsFileName).toPath()).use { writer ->
            CSVWriter(writer).use { csvWriter ->
                val headerRecord = arrayOf("Data", "Użytkownik", "Nazwa testu", "Status", "Komunikat")
                csvWriter.writeNext(headerRecord)

                fullTestResponses.forEach { fullTestResponse ->
                    csvWriter.writeNext(arrayOf(Date(fullTestResponse.date).toString(), fullTestResponse.userName))
                    fullTestResponse.testResponses.forEach {
                        csvWriter.writeNext(arrayOf("", "", it.testCaseName, it.status, it.message))
                    }
                }
            }
        }

        return File(resultDir, statisticsFileName)
    }

    private fun hasStageStatistics(groupName: String, projectName: String, stageName: String): Boolean {
        return hasStatistics(stagePathProvider.getStudentResultsDir(groupName, projectName, stageName))
    }

    private fun hasIntegrationStatistics(groupName: String, projectName: String, stageName: String): Boolean {
        return hasStatistics(integrationPathProvider.getStudentResultsDir(groupName, projectName, stageName))
    }

    private fun hasStatistics(resultsDir: File): Boolean {
        return resultsDir.exists() && resultsDir.list().any { it == RESULT_FILE_NAME }
    }

    companion object {
        val log = LoggerFactory.logger(GroupResultService::class.java)
        const val RESULT_FILE_NAME = "result.json"
        const val SUCCESS_RESPONSE = "200"
    }
}

data class FullTestResponse(val date: Long, val userName: String, val testResponses: List<TestResponse>)

enum class FileType {
    BINARY,
    REPORT,
    RESULTS
}

data class StudentTestCase(val name: String, val parameters: String?)