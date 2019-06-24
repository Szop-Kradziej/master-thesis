package com.drabarz.karolina.testplatformrunner

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
        val pathProvider: PathProvider,
        val jarService: JarService) {

    fun runJar(projectName: String, stageName: String): List<TestResponse> {
        val testResponses = jarService.runJar(projectName, stageName)
        saveTestResponses(projectName, stageName, testResponses)

        return testResponses
    }

    private fun saveTestResponses(projectName: String, stageName: String, testResponses: List<TestResponse>) {
        val resultsDir = pathProvider.getStudentResultsDir(projectName, stageName)
        resultsDir.mkdirs()

        val file = File(resultsDir, "result.json");

        val out = ByteArrayOutputStream()
        val mapper = ObjectMapper()

        mapper.writeValue(out, testResponses)

        file.writeBytes(out.toByteArray())
    }

    fun saveFile(projectName: String, stageName: String, uploadedFile: MultipartFile, fileType: FileType) {
        var dir = pathProvider.getStudentStageDir(projectName, stageName)
        if (fileType == FileType.BINARY) {
            dir = pathProvider.getStudentBinDir(projectName, stageName)
        } else {
            dir = pathProvider.getStudentReportDir(projectName, stageName)
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
                            getTestCasesWithResults(projectName, stage.stageName, stage.testCases.map { it.testCaseName }).sortedBy { it.testCaseName },
                            getTestCasesWithResults(projectName, stage.stageName, stage.testCases.map { it.testCaseName }).count { it.status == "SUCCESS" },
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
        val dir = pathProvider.getStudentCodeDir(projectName, stageName)

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

        val stageDir = pathProvider.getStudentStageDir(projectName, stageName)
        if (!stageDir.exists()) {
            return null
        }

        val fileDir: File
        if (fileType == FileType.BINARY) {
            fileDir = pathProvider.getStudentBinDir(projectName, stageName)
        } else {
            fileDir = pathProvider.getStudentReportDir(projectName, stageName)
        }

        if (!fileDir.exists() || fileDir.list().size != 1) {
            return null
        }

        return fileDir.list().first()
    }

    private fun getTestCasesWithResults(projectName: String, stageName: String, testCases: List<String>): List<TestCaseWithResult> {
        val resultFile = File(pathProvider.getStudentResultsDir(projectName, stageName), "result.json")

        if (!resultFile.exists()) {
            return testCases.map { testCase -> TestCaseWithResult(testCase, "NO RUN", null) }
        }

        val jsonData = resultFile.readBytes()
        val results = jacksonObjectMapper().readerFor(Array<TestResponse>::class.java).readValue<Array<TestResponse>>(jsonData).toList()

        return testCases.map { testCase ->
            TestCaseWithResult(
                    testCase,
                    results.find { it.testCaseName == testCase }?.status ?: "NO RUN",
                    results.find { it.testCaseName == testCase }?.message,
                    isLogsFileExist(projectName, stageName, testCase))
        }
    }

    private fun isLogsFileExist(projectName: String, stageName: String, testCase: String): Boolean {
        return pathProvider.getStudentLogsFileDir(projectName, stageName, testCase).exists()
    }

    fun saveCodeLink(projectName: String, stageName: String, codeLink: String): String {
        val dir = pathProvider.getStudentCodeDir(projectName, stageName)
        dir.mkdirs()

        val outputFile = File(dir.path, PathProvider.CODE)
        outputFile.writeText(codeLink)

        return "200"
    }

    fun getJar(projectName: String, stageName: String): File {
        return getSingleFile(pathProvider.getStudentBinDir(projectName, stageName))
    }

    fun getReport(projectName: String, stageName: String): File {
        return getSingleFile(pathProvider.getStudentReportDir(projectName, stageName))
    }

    fun getLogsFile(projectName: String, stageName: String, testCaseName: String): File {
        return getExactFile(pathProvider.getStudentLogsFileDir(projectName, stageName, testCaseName))
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

    companion object {
        val log = LoggerFactory.getLogger(StudentService::class.java)
    }
}

enum class FileType {
    BINARY,
    REPORT,
    RESULTS
}
