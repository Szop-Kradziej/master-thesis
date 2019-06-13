package com.drabarz.karolina.testplatformrunner

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayOutputStream
import java.io.File

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

        //TODO: Delete this file
        if (dir.list().isNotEmpty()) {
            File("${dir.path}/${dir.list()[0]}").delete()
        }

        val outputFile = File(dir.path, uploadedFile.originalFilename)
        uploadedFile.transferTo(outputFile)
    }

    fun getStudentStages(projectName: String): List<StudentStage> {
        return stageService
                .getStages(projectName)
                .map { stage ->
                    StudentStage(
                            stage.stageName,
                            getBinaryName(projectName, stage.stageName),
                            getReportName(projectName, stage.stageName),
                            getTestCasesWithResults(projectName, stage.stageName, stage.testCases.map { it.testCaseName }),
                            getTestCasesWithResults(projectName, stage.stageName, stage.testCases.map { it.testCaseName }).count {it.status == "SUCCESS" },
                            stage.testCases.size,
                            getDeadline(),
                            getCodeLink())
                }
    }

    private fun getCodeLink(): String {
        //TODO: Implement after connection to db added
        return "https://github.com/Szop-Kradziej/rail-learn/commits/master"
    }

    private fun getDeadline(): String {
        //TODO: Implement after connection to db added
        return "10/06/2019 23:59"
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

        return fileDir.list()[0]
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
                    results.find { it -> it.testCaseName == testCase }?.status ?: "NO RUN",
                    results.find { it -> it.testCaseName == testCase }?.message)
        }
    }

    fun saveCodeLink(projectName: String, stageName: String, codeLink: String) {
        //TODO: Implement when conection to db will be set
    }

    fun getJar(projectName: String, stageName: String): File {
        return getSingleFile(pathProvider.getStudentBinDir(projectName, stageName))
    }

    fun getReport(projectName: String, stageName: String): File {
        return getSingleFile(pathProvider.getStudentReportDir(projectName, stageName))
    }

    fun getSingleFile(dir: File): File {
        if (!dir.exists() && dir.list().size != 1) {
            throw java.lang.RuntimeException("Error file doesn't exist")
        }

        return File(dir, dir.list()[0])
    }
}

enum class FileType {
    BINARY,
    REPORT,
    RESULTS
}