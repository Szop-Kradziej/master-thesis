package com.drabarz.karolina.testplatformrunner

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayOutputStream
import java.io.File

@Component
class StudentService(val testCaseService: TestCaseService, val pathProvider: PathProvider, val jarService: JarService) {

    fun runJar(projectName: String, stageName: String): List<TestResponse> {
        val testResponses = jarService.runJar(projectName, stageName)
        saveTestResponses(projectName, stageName, testResponses)

        return testResponses
    }

    private fun saveTestResponses(projectName: String, stageName: String, testResponses: List<TestResponse>) {
        val pathPrefix = "${pathProvider.jarPath}/$projectName/$stageName"
        val dir = File("$pathPrefix/results")
        dir.mkdirs()

        val file = File("$pathPrefix/results", "result.json");

        val out = ByteArrayOutputStream()
        val mapper = ObjectMapper()

        mapper.writeValue(out, testResponses)

        file.writeBytes(out.toByteArray())
    }

    fun saveFile(projectName:String, stageName: String, uploadedFile: MultipartFile, fileType: FileType) {
        val pathPrefix = "${pathProvider.jarPath}/$projectName/$stageName"
        var dir = File(pathPrefix)
        if (fileType == FileType.BINARY) {
            dir = File("$pathPrefix/bin")
        }
        else {
            dir = File("$pathPrefix/report")
        }

        dir.mkdirs()

        //TODO: Delete this file
        if(dir.list().isNotEmpty()) {
            JarService.log.info("Existing file to delete: " + File("${dir.path}/bin/${dir.list()[0]}").path)
        }

        val outputFile = File(dir.path, uploadedFile.originalFilename)
        uploadedFile.transferTo(outputFile)
    }

    fun getStudentStages(projectName: String): List<StudentStage> {
        return testCaseService
                .getStages(projectName)
                .map { stage -> StudentStage(stage.stageName, getBinaryName(projectName, stage.stageName), getReportName(projectName, stage.stageName), getTestCasesWithResults(projectName, stage.stageName, stage.testCases))}
    }

    private fun getBinaryName(projectName: String, stageName: String): String? {
        return getStageFileName(projectName, stageName, FileType.BINARY)
    }

    private fun getReportName(projectName: String, stageName: String): String? {
        return getStageFileName(projectName, stageName, FileType.REPORT)
    }

    private fun getStageFileName(projectName: String, stageName: String, fileType: FileType): String? {

        val stageDir = File("${pathProvider.jarPath}/$projectName/$stageName")

        if (!stageDir.exists()) {
            return null
        }

        val fileDir: File
        if (fileType == FileType.BINARY) {
            fileDir = File("$stageDir/bin")
        }
        else {
            fileDir = File("$stageDir/report")
        }

        if (!fileDir.exists() || fileDir.list().size != 1) {
            return null
        }

        return fileDir.list()[0]
    }

    private fun getTestCasesWithResults(projectName: String, stageName: String, testCases: List<String>): List<TestCaseWithResult> {
        val resultFile = File("${pathProvider.jarPath}/$projectName/$stageName/results/result.json")

        if (!resultFile.exists()) {
            return testCases.map { testCase -> TestCaseWithResult(testCase, "NO RUN", null)}
        }

        val jsonData = resultFile.readBytes()
        val results = jacksonObjectMapper().readerFor(Array<TestResponse>::class.java).readValue<Array<TestResponse>>(jsonData).toList()

        return testCases.map {testCase ->
            TestCaseWithResult(
                    testCase,
                    results.find { it -> it.testCaseName == testCase }?.status ?: "NO RUN",
                    results.find { it -> it.testCaseName == testCase }?.message)
        }
    }
}

enum class FileType {
    BINARY,
    REPORT,
    RESULTS
}