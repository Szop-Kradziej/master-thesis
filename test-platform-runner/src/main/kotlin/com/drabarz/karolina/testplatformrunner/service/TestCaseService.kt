package com.drabarz.karolina.testplatformrunner.service

import com.drabarz.karolina.testplatformrunner.api.IntegrationTestCase
import com.drabarz.karolina.testplatformrunner.api.TestCase
import com.drabarz.karolina.testplatformrunner.service.helper.DeleteFileHelper
import com.drabarz.karolina.testplatformrunner.service.helper.PathProvider
import com.drabarz.karolina.testplatformrunner.service.helper.TaskPathProvider
import org.slf4j.LoggerFactory
import org.springframework.web.multipart.MultipartFile
import java.io.File

class TestCaseService(val pathProvider: TaskPathProvider) {

    private val deleteFileHelper = DeleteFileHelper()

    fun getTestCases(projectName: String, taskName: String): List<TestCase> {
        log.info("Getting all test cases for task: $taskName in project: $projectName")

        val projectDir = pathProvider.getProjectDir(projectName)
        if (!projectDir.exists()) {
            log.error("Can not get test cases for project: $projectName, project doesn't exist")
            throw RuntimeException("Error. Project $projectName doesn't exist")
        }

        val taskDir = pathProvider.getTaskDir(projectName, taskName)
        if (!taskDir.exists()) {
            log.error("Can not get test cases for task: $taskName in project: $projectName, task doesn't exist")
            throw RuntimeException("Error. Task $taskName doesn't exist")
        }

        val testCasesDir = pathProvider.getTaskTestCasesDir(projectName, taskName)
        if (!testCasesDir.exists()) {
            log.warn("No test cases for task: $taskName in project: $projectName")
            return emptyList()
        }

        return testCasesDir.list()
                .map {
                    TestCase(
                            it,
                            getParametersTestCaseFileName(projectName, taskName, it),
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


    private fun getParametersTestCaseFileName(projectName: String, taskName: String, testCaseName: String): String? {
        val fileDir = pathProvider.getTaskTestCaseParametersDir(projectName, taskName, testCaseName)
        if (!fileDir.exists() || fileDir.list().size != 1 || fileDir.listFiles().first().reader().readText().isBlank()) {
            return null
        }

        return fileDir.list().first()
    }

    private fun getTestCaseFileName(fileType: String, projectName: String, taskName: String, testCaseName: String): String? {
        val fileDir = pathProvider.getTaskTestCaseFileDir(projectName, taskName, testCaseName, fileType)
        if (!fileDir.exists() || fileDir.list().size != 1) {
            return null
        }
        return fileDir.list().first()
    }

    fun getIntegrationTestCases(projectName: String, taskName: String, numberOfStages: Int): List<IntegrationTestCase> {
        log.info("Getting all test cases for task: $taskName in project: $projectName")

        val projectDir = pathProvider.getProjectDir(projectName)
        if (!projectDir.exists()) {
            log.error("Can not get test cases for project: $projectName, project doesn't exist")
            throw RuntimeException("Error. Project $projectName doesn't exist")
        }

        val taskDir = pathProvider.getTaskDir(projectName, taskName)
        if (!taskDir.exists()) {
            log.error("Can not get test cases for task: $taskName in project: $projectName, task doesn't exist")
            throw RuntimeException("Error. Task $taskName doesn't exist")
        }

        val testCasesDir = pathProvider.getTaskTestCasesDir(projectName, taskName)
        if (!testCasesDir.exists()) {
            log.warn("No test cases for task: $taskName in project: $projectName")
            return emptyList()
        }

        return testCasesDir.list()
                .map {
                    IntegrationTestCase(
                            it,
                            getIntegrationParametersTestCaseFileName(projectName, taskName, it, numberOfStages),
                            getTestCaseFileName(INPUT, projectName, taskName, it),
                            getTestCaseFileName(OUTPUT, projectName, taskName, it))
                }
    }

    private fun getIntegrationParametersTestCaseFileName(projectName: String, taskName: String, testCaseName: String, numberOfStages: Int): List<String?> {
        var listOfParameters = mutableListOf<String?>()

        for (i in 0..numberOfStages - 1) {
            var integrationStageParameterDir = File(pathProvider.getTaskTestCaseParametersDir(projectName, taskName, testCaseName), "stage_$i")
            if (integrationStageParameterDir.exists() && integrationStageParameterDir.list().size == 1 && integrationStageParameterDir.listFiles().first().readText().isNotBlank()) {
                listOfParameters.add(integrationStageParameterDir.list().first())
            } else {
                listOfParameters.add(null)
            }
        }

        return listOfParameters
    }

    fun addTestCase(inputFile: MultipartFile, outputFile: MultipartFile, projectName: String, taskName: String, testCaseName: String): String {
        log.info("Adding test case: $testCaseName for task: $taskName in project: $projectName")

        val projectDir = pathProvider.getProjectDir(projectName)
        if (!projectDir.exists()) {
            log.error("Can not create test cases for project: $projectName, project doesn't exist")
            throw RuntimeException("Error. Can not create test case for project. Project $projectName doesn't exist")
        }

        val taskDir = pathProvider.getTaskDir(projectName, taskName)
        if (!taskDir.exists()) {
            log.error("Can not create test cases for task: $taskName in project: $projectName, task doesn't exist")
            throw RuntimeException("Error. Can not create test case for task. Task $taskName doesn't exist")
        }

        val testCaseDir = pathProvider.getTaskTestCaseDir(projectName, taskName, testCaseName)
        testCaseDir.mkdirs()

        addTestCaseFile(inputFile, INPUT, projectName, taskName, testCaseName)
        addTestCaseFile(outputFile, OUTPUT, projectName, taskName, testCaseName)

        log.info("Test case: $testCaseName for task: $taskName in project: $projectName created")

        return SUCCESS_RESPONSE
    }

    private fun addTestCaseFile(file: MultipartFile, type: String, projectName: String, taskName: String, testCaseName: String) {
        val fileDir = pathProvider.getTaskTestCaseFileDir(projectName, taskName, testCaseName, type)
        fileDir.mkdirs()

        val savedInputFile = File(fileDir, file.originalFilename)
        file.transferTo(savedInputFile)
    }

    fun getTestCaseFile(projectName: String, taskName: String, testCaseName: String, fileType: String): File {
        log.info("Getting ${fileType.toLowerCase()} file for test case: $testCaseName in task: $taskName in project: $projectName")

        val fileDir = pathProvider.getTaskTestCaseFileDir(projectName, taskName, testCaseName, fileType)
        if (fileDir.exists() && fileDir.list().size == 1) {
            return fileDir.listFiles().first()
        }

        log.error("Test case ${fileType.toLowerCase()} file for test case: $testCaseName in task: $taskName in project: $projectName doesn't exist")
        throw java.lang.RuntimeException("Error file doesn't exist")
    }

    fun getIntegrationParametersTestCaseFile(projectName: String, taskName: String, testCaseName: String, index: Int): File {
        log.info("Getting parameters file for test case: $testCaseName in task: $taskName in project: $projectName")

        val fileDir = File(pathProvider.getTaskTestCaseParametersDir(projectName, taskName, testCaseName), "stage_$index")
        if (fileDir.exists() && fileDir.list().size == 1) {
            return fileDir.listFiles().first()
        }

        log.error("Test case parameters file for test case: $testCaseName in task: $taskName in project: $projectName doesn't exist")
        throw java.lang.RuntimeException("Error file doesn't exist")
    }

    fun uploadTestCaseFile(projectName: String, taskName: String, testCaseName: String, fileType: String, file: MultipartFile): String {
        log.info("Adding ${fileType.toLowerCase()} file for test case: $testCaseName in task: $taskName in project: $projectName")

        val fileDir = pathProvider.getTaskTestCaseFileDir(projectName, taskName, testCaseName, fileType)

        if (fileDir.exists()) {
            deleteFileHelper.deleteSingleFileFromDir(fileDir)
        }

        addTestCaseFile(file, fileType, projectName, taskName, testCaseName)

        log.info("Test case ${fileType.toLowerCase()} file for test case: $testCaseName in task: $taskName in project: $projectName created")

        return SUCCESS_RESPONSE
    }

    fun uploadIntegrationParametersTestCaseFile(projectName: String, taskName: String, testCaseName: String, index: Int, file: MultipartFile): String {
        log.info("Adding parameters file for test case: $testCaseName in task: $taskName in project: $projectName")

        val fileDir = File(pathProvider.getTaskTestCaseFileDir(projectName, taskName, testCaseName, PARAMETERS), "stage_$index")

        if (fileDir.exists()) {
            deleteFileHelper.deleteSingleFileFromDir(fileDir)
        }

        fileDir.mkdirs()

        val savedInputFile = File(fileDir, file.originalFilename)
        file.transferTo(savedInputFile)

        log.info("Test case parameters file for test case: $testCaseName in task: $taskName in project: $projectName created")

        return SUCCESS_RESPONSE
    }

    fun editParameters(projectName: String, taskName: String, testCaseName: String, parameters: String?): String {
        log.info("Editing parameters for test case: $testCaseName in task: $taskName in project: $projectName")

        val dir = pathProvider.getTaskTestCaseParametersDir(projectName, taskName, testCaseName)
        dir.mkdirs()

        val outputFile = File(dir.path, PathProvider.PARAMETERS)
        if (parameters.isNullOrBlank()) {
            outputFile.writeText("")
            return SUCCESS_RESPONSE
        }

        outputFile.writeText(parameters)

        log.info("Parameters for test case: $testCaseName in task: $taskName in project: $projectName edited")

        return SUCCESS_RESPONSE
    }

    fun deleteTestCases(projectName: String, taskName: String) {
        log.info("Deleting test cases for task: $taskName in project: $projectName")

        val testCasesDir = pathProvider.getTaskTestCasesDir(projectName, taskName)
        if (!testCasesDir.exists()) {
            log.info("Test cases directory for task: $taskName in project $projectName doesn't exist")
            return
        }

        testCasesDir.list()
                .forEach { deleteTestCase(projectName, taskName, it) }

        testCasesDir.delete()

        log.info("Test cases for task: $taskName in project: $projectName deleted")
    }

    fun deleteTestCase(projectName: String, taskName: String, testCaseName: String): String {
        log.info("Deleting test case: $testCaseName for task: $taskName in project: $projectName")

        deleteTestCaseFileDirIfExists(pathProvider.getTaskTestCaseFileDir(projectName, taskName, testCaseName, INPUT))
        deleteTestCaseFileDirIfExists(pathProvider.getTaskTestCaseFileDir(projectName, taskName, testCaseName, OUTPUT))
        deleteTestCaseFileDirIfExists(pathProvider.getTaskTestCaseParametersDir(projectName, taskName, testCaseName))
        pathProvider.getTaskTestCaseDir(projectName, taskName, testCaseName).delete()

        log.info("Test case: $testCaseName for task: $taskName in project: $projectName deleted")

        return SUCCESS_RESPONSE
    }

    private fun deleteTestCaseFileDirIfExists(fileDir: File) {
        if (fileDir.exists()) {
            deleteFileHelper.deleteSingleFileWithDirectory(fileDir)
        }
    }

    companion object {
        val log = LoggerFactory.getLogger(TestCaseService::class.java)
        const val SUCCESS_RESPONSE = "200"
        const val INPUT = "input"
        const val PARAMETERS = "parameters"
        const val OUTPUT = "output"
    }
}