package com.drabarz.karolina.testplatformrunner.service

import com.drabarz.karolina.testplatformrunner.api.TestCase
import com.drabarz.karolina.testplatformrunner.service.helper.DeleteFileHelper
import com.drabarz.karolina.testplatformrunner.service.helper.PathProvider
import com.drabarz.karolina.testplatformrunner.service.helper.TaskPathProvider
import org.springframework.web.multipart.MultipartFile
import java.io.File

class TestCaseService(val pathProvider: TaskPathProvider) {

    private val deleteFileHelper = DeleteFileHelper()

    fun saveTestCase(inputFile: MultipartFile, outputFile: MultipartFile, projectName: String, taskName: String, testCaseName: String): String {
        val projectDir = pathProvider.getProjectDir(projectName)
        if (!projectDir.exists()) {
            throw RuntimeException("Error. Can not create test case for project. Project $projectName doesn't exist")
        }

        val taskDir = pathProvider.getTaskDir(projectName, taskName)
        if (!taskDir.exists()) {
            throw RuntimeException("Error. Can not create test case for task. Task $taskName doesn't exist")
        }

        val testCaseDir = pathProvider.getTaskTestCaseDir(projectName, taskName, testCaseName)
        testCaseDir.mkdirs()

        saveTestCaseFile(inputFile, INPUT, projectName, taskName, testCaseName)
        saveTestCaseFile(outputFile, OUTPUT, projectName, taskName, testCaseName)

        return "200"
    }

    fun saveTestCaseFile(file: MultipartFile, type: String, projectName: String, taskName: String, testCaseName: String) {
        val fileDir = pathProvider.getTaskTestCaseFileDir(projectName, taskName, testCaseName, type)
        fileDir.mkdirs()

        val savedInputFile = File(fileDir, file.originalFilename)
        file.transferTo(savedInputFile)
    }

    fun getTestCases(projectName: String, taskName: String): List<TestCase> {
        val projectDir = pathProvider.getProjectDir(projectName)
        if (!projectDir.exists()) {
            throw RuntimeException("Error. Project $projectName doesn't exist")
        }

        val taskDir = pathProvider.getTaskDir(projectName, taskName)
        if (!taskDir.exists()) {
            throw RuntimeException("Error. taskDao $taskName doesn't exist")
        }

        val testCasesDir = pathProvider.getTaskTestCasesDir(projectName, taskName)
        if (!testCasesDir.exists()) {
            return emptyList()
        }

        return testCasesDir.list()
                .map {
                    TestCase(
                            it,
                            getTestCaseParameters(projectName, taskName, it),
                            getTestCaseFileName(INPUT, projectName, taskName, it),
                            getTestCaseFileName(OUTPUT, projectName, taskName, it))
                }
    }

    private fun getTestCaseParameters(projectName: String, taskName: String, testCaseName: String): String? {
        val dir = pathProvider.getTaskTestCaseParametersDir(projectName, taskName, testCaseName)

        val testCaseParametersFile = File(dir.path, PathProvider.PARAMETERS)

        if (!testCaseParametersFile.exists() || testCaseParametersFile.readText().isBlank()) {
            return null
        }

        return testCaseParametersFile.readText()
    }

    private fun getTestCaseFileName(fileType: String, projectName: String, taskName: String, testCaseName: String): String? {
        val fileDir = pathProvider.getTaskTestCaseFileDir(projectName, taskName, testCaseName, fileType)
        if (!fileDir.exists() || fileDir.list().size != 1) {
            return null
        }
        return fileDir.list().first()
    }

    fun getTestCaseFile(projectName: String, taskName: String, testCaseName: String, fileType: String): File {
        val fileDir = pathProvider.getTaskTestCaseFileDir(projectName, taskName, testCaseName, fileType)
        if (fileDir.exists() && fileDir.list().size == 1) {
            return fileDir.listFiles().first()
        }

        throw java.lang.RuntimeException("Error file doesn't exist")
    }

    fun deleteTestCases(projectName: String, taskName: String) {
        val testCasesDir = pathProvider.getTaskTestCasesDir(projectName, taskName)
        if (!testCasesDir.exists()) {
            return
        }

        testCasesDir.list()
                .forEach { deleteTestCase(projectName, taskName, it) }

        testCasesDir.delete()
    }

    fun deleteTestCase(projectName: String, taskName: String, testCaseName: String): String {

        deleteTestCaseFileDirIfExists(pathProvider.getTaskTestCaseFileDir(projectName, taskName, testCaseName, INPUT))
        deleteTestCaseFileDirIfExists(pathProvider.getTaskTestCaseFileDir(projectName, taskName, testCaseName, OUTPUT))
        deleteTestCaseFileDirIfExists(pathProvider.getTaskTestCaseParametersDir(projectName, taskName, testCaseName))
        pathProvider.getTaskTestCaseDir(projectName, taskName, testCaseName).delete()

        return "200"
    }

    private fun deleteTestCaseFileDirIfExists(fileDir: File) {
        if (fileDir.exists()) {
            deleteFileHelper.deleteSingleFileWithDirectory(fileDir)
        }
    }

    fun uploadTestCaseFile(projectName: String, taskName: String, testCaseName: String, fileType: String, file: MultipartFile): String {
        val fileDir = pathProvider.getTaskTestCaseFileDir(projectName, taskName, testCaseName, fileType)

        if(fileDir.exists()) {
            deleteFileHelper.deleteSingleFileFromDir(fileDir)
        }

        saveTestCaseFile(file, fileType, projectName, taskName, testCaseName)

        return "200"
    }

    fun editParameters(projectName: String, taskName: String, testCaseName: String, parameters: String?): String {
        val dir = pathProvider.getTaskTestCaseParametersDir(projectName, taskName, testCaseName)
        dir.mkdirs()

        val outputFile = File(dir.path, PathProvider.PARAMETERS)
        if (parameters.isNullOrBlank()) {
            outputFile.writeText("")
            return "200"
        }

        outputFile.writeText(parameters)

        return "200"
    }

    companion object {
        val INPUT = "input"
        val OUTPUT = "output"
    }
}