package com.drabarz.karolina.testplatformrunner

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

        val savedInputFile = File(testCaseDir, INPUT_FILE_NAME)
        inputFile.transferTo(savedInputFile)

        val savedOutputFile = File(testCaseDir, OUTPUT_FILE_NAME)
        outputFile.transferTo(savedOutputFile)

        return "200"
    }

    fun getTestCasesNames(projectName: String, stageName: String): List<String> {
        val projectDir = pathProvider.getProjectDir(projectName)
        if (!projectDir.exists()) {
            throw RuntimeException("Error. Project $projectName doesn't exist")
        }

        val stageDir = pathProvider.getStageDir(projectName, stageName)
        if (!stageDir.exists()) {
            throw RuntimeException("Error. Stage $stageName doesn't exist")
        }

        return stageDir.list().asList()
    }

    fun getTestCaseFile(projectName: String, stageName: String, testCaseName: String, fileName: String): File {
        val file = pathProvider.getTestCaseFileDir(projectName, stageName, testCaseName, fileName)
        if (file.exists()) {
            return file
        }

        throw java.lang.RuntimeException("Error file doesn't exist")
    }

    companion object {
        val INPUT_FILE_NAME = "input"
        val OUTPUT_FILE_NAME = "output"
    }
}