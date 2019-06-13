package com.drabarz.karolina.testplatformrunner

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File

@Component
class TestCaseService(val pathProvider: PathProvider) {

    fun saveTestCase(inputFile: MultipartFile, outputFile: MultipartFile, projectName: String, stageName: String, testCaseName: String): String {
        val projectDir = pathProvider.getProjectDir(projectName)
        if (!projectDir.exists()) {
            throw RuntimeException("Error. Can not create test case for project. Project $projectName doesn't exist")
        }

        val stageDir = pathProvider.getStageDir(projectName, stageName)
        if (!stageDir.exists()) {
            throw RuntimeException("Error. Can not create test case for stage. Stage $stageName doesn't exist")
        }

        val testCaseDir = pathProvider.getTestCaseDir(projectName, stageName, testCaseName)
        testCaseDir.mkdirs()

        saveTestCaseFile(inputFile, INPUT, projectName, stageName, testCaseName)
        saveTestCaseFile(outputFile, OUTPUT, projectName, stageName, testCaseName)

        return "200"
    }

    fun saveTestCaseFile(file: MultipartFile, type: String, projectName: String, stageName: String, testCaseName: String) {
        val fileDir = pathProvider.getTestCaseFileDir(projectName, stageName, testCaseName, type)
        fileDir.mkdirs()

        val savedInputFile = File(fileDir, file.originalFilename)
        file.transferTo(savedInputFile)
    }

    fun getTestCasesNames(projectName: String, stageName: String): List<TestCase> {
        val projectDir = pathProvider.getProjectDir(projectName)
        if (!projectDir.exists()) {
            throw RuntimeException("Error. Project $projectName doesn't exist")
        }

        val stageDir = pathProvider.getStageDir(projectName, stageName)
        if (!stageDir.exists()) {
            throw RuntimeException("Error. Stage $stageName doesn't exist")
        }

        val testCasesDir = pathProvider.getTestCasesDir(projectName, stageName)
        if (!testCasesDir.exists()) {
            return emptyList()
        }

        return testCasesDir.list()
                .map {
                    TestCase(
                            it,
                            getTestCaseFileName(INPUT, projectName, stageName, it),
                            getTestCaseFileName(OUTPUT, projectName, stageName, it))
                }
    }

    private fun getTestCaseFileName(fileType: String, projectName: String, stageName: String, testCaseName: String): String? {
        val fileDir = pathProvider.getTestCaseFileDir(projectName, stageName, testCaseName, fileType)
        if (!fileDir.exists() || fileDir.list().size != 1) {
            return null
        }
        return fileDir.list().first()
    }

    fun getTestCaseFile(projectName: String, stageName: String, testCaseName: String, fileType: String): File {
        val fileDir = pathProvider.getTestCaseFileDir(projectName, stageName, testCaseName, fileType)
        if (fileDir.exists() && fileDir.list().size == 1) {
            return fileDir.listFiles().first()
        }

        throw java.lang.RuntimeException("Error file doesn't exist")
    }

    fun deleteTestCase(projectName: String, stageName: String, testCaseName: String): String {

        deleteTestCaseFileDirIfExists(pathProvider.getTestCaseFileDir(projectName, stageName, testCaseName, INPUT))
        deleteTestCaseFileDirIfExists(pathProvider.getTestCaseFileDir(projectName, stageName, testCaseName, OUTPUT))
        pathProvider.getTestCaseDir(projectName, stageName, testCaseName).delete()

        return "200"
    }

    private fun deleteTestCaseFileDirIfExists(fileDir: File) {
        if (fileDir.exists()) {
            if (fileDir.list().isNotEmpty()) {
                log.info("Existing file to delete: " + fileDir.list().first() + " from: " + fileDir.absolutePath)
                fileDir.listFiles().first().delete()
            }
            fileDir.delete()
        }
    }

    companion object {
        val INPUT = "input"
        val OUTPUT = "output"

        val log = LoggerFactory.getLogger(TestCaseService::class.java)
    }
}