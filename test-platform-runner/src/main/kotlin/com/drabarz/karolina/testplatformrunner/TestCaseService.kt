package com.drabarz.karolina.testplatformrunner

import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File

@Component
class TestCaseService(
        val pathProvider: PathProvider,
        val deleteFileHelper: DeleteFileHelper) {

    fun saveTestCase(inputFile: MultipartFile, outputFile: MultipartFile, projectName: String, stageName: String, testCaseName: String): String {
        val projectDir = pathProvider.getProjectDir(projectName)
        if (!projectDir.exists()) {
            throw RuntimeException("Error. Can not create test case for project. Project $projectName doesn't exist")
        }

        val stageDir = pathProvider.getStageDir(projectName, stageName)
        if (!stageDir.exists()) {
            throw RuntimeException("Error. Can not create test case for stage. StageDao $stageName doesn't exist")
        }

        val testCaseDir = pathProvider.getStageTestCaseDir(projectName, stageName, testCaseName)
        testCaseDir.mkdirs()

        saveTestCaseFile(inputFile, INPUT, projectName, stageName, testCaseName)
        saveTestCaseFile(outputFile, OUTPUT, projectName, stageName, testCaseName)

        return "200"
    }

    fun saveTestCaseFile(file: MultipartFile, type: String, projectName: String, stageName: String, testCaseName: String) {
        val fileDir = pathProvider.getStageTestCaseFileDir(projectName, stageName, testCaseName, type)
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
            throw RuntimeException("Error. StageDao $stageName doesn't exist")
        }

        val testCasesDir = pathProvider.getStageTestCasesDir(projectName, stageName)
        if (!testCasesDir.exists()) {
            return emptyList()
        }

        return testCasesDir.list()
                .map {
                    TestCase(
                            it,
                            getTestCaseParameters(projectName, stageName, it),
                            getTestCaseFileName(INPUT, projectName, stageName, it),
                            getTestCaseFileName(OUTPUT, projectName, stageName, it))
                }
    }

    private fun getTestCaseParameters(projectName: String, stageName: String, testCaseName: String): String? {
        val dir = pathProvider.getStageTestCaseParametersDir(projectName, stageName, testCaseName)

        val testCaseParametersFile = File(dir.path, PathProvider.PARAMETERS)

        if (!testCaseParametersFile.exists() || testCaseParametersFile.readText().isBlank()) {
            return null
        }

        return testCaseParametersFile.readText()
    }

    private fun getTestCaseFileName(fileType: String, projectName: String, stageName: String, testCaseName: String): String? {
        val fileDir = pathProvider.getStageTestCaseFileDir(projectName, stageName, testCaseName, fileType)
        if (!fileDir.exists() || fileDir.list().size != 1) {
            return null
        }
        return fileDir.list().first()
    }

    fun getTestCaseFile(projectName: String, stageName: String, testCaseName: String, fileType: String): File {
        val fileDir = pathProvider.getStageTestCaseFileDir(projectName, stageName, testCaseName, fileType)
        if (fileDir.exists() && fileDir.list().size == 1) {
            return fileDir.listFiles().first()
        }

        throw java.lang.RuntimeException("Error file doesn't exist")
    }

    fun deleteTestCase(projectName: String, stageName: String, testCaseName: String): String {

        deleteTestCaseFileDirIfExists(pathProvider.getStageTestCaseFileDir(projectName, stageName, testCaseName, INPUT))
        deleteTestCaseFileDirIfExists(pathProvider.getStageTestCaseFileDir(projectName, stageName, testCaseName, OUTPUT))
        pathProvider.getStageTestCaseDir(projectName, stageName, testCaseName).delete()

        return "200"
    }

    private fun deleteTestCaseFileDirIfExists(fileDir: File) {
        if (fileDir.exists()) {
            deleteFileHelper.deleteSingleFileWithDirectory(fileDir)
        }
    }

    fun uploadTestCaseFile(projectName: String, stageName: String, testCaseName: String, fileType: String, file: MultipartFile): String {
        val fileDir = pathProvider.getStageTestCaseFileDir(projectName, stageName, testCaseName, fileType)

        if(fileDir.exists()) {
            deleteFileHelper.deleteSingleFileFromDir(fileDir)
        }

        saveTestCaseFile(file, fileType, projectName, stageName, testCaseName)

        return "200"
    }

    fun editParameters(projectName: String, stageName: String, testCaseName: String, parameters: String?): String {
        val dir = pathProvider.getStageTestCaseParametersDir(projectName, stageName, testCaseName)
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