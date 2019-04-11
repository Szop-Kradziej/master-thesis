package com.drabarz.karolina.testplatformrunner

import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.lang.RuntimeException

@Component
class TestCaseService {

    val pathProvider: PathProv = PathProvider()

    fun addProject(projectName: String): TestResponse {
        val dir = File("${pathProvider.projectPath}/$projectName")

        if(dir.exists()) {
            return Error("Warning. Project already exists")
        }

        dir.mkdir()

        return Success()
    }

    fun addStage(projectName: String, stageName: String): TestResponse {
        val stageDir = File("${pathProvider.projectPath}/$projectName")
        if(! stageDir.exists()) {
            return Error("Error. Can not create stage for project. Project $projectName doesn't exist")
        }

        val dir = File("${pathProvider.projectPath}/$projectName/$stageName")

        if(dir.exists()) {
            return Error("Warning. Stage already exists")
        }

        dir.mkdir()

        return Success()
    }

    fun saveTestCase(inputFile: MultipartFile, outputFile: MultipartFile, projectName: String, stageName: String, testCaseName: String): TestResponse {
        val testCasePath = "${pathProvider.projectPath}/$projectName/$stageName/$testCaseName"

        val projectDir = File("${pathProvider.projectPath}/$projectName")
        if(! projectDir.exists()) {
            return Error("Error. Can not create test case for project. Project $projectName doesn't exist")
        }

        val stageDir = File("${pathProvider.projectPath}/$projectName/$stageName")
        if(! stageDir.exists()) {
            return Error("Error. Can not create test case for stage. Stage $stageName doesn't exist")
        }

        val testCaseDir = File(testCasePath)
        testCaseDir.mkdirs()

        val savedInputFile = File(testCasePath, INPUT_FILE_NAME)
        inputFile.transferTo(savedInputFile)

        val savedOutputFile = File(testCasePath, OUTPUT_FILE_NAME)
        outputFile.transferTo(savedOutputFile)

        return Success()
    }

    fun getTestCasesNames(projectName: String, stageName: String): List<String> {
        val projectDir = File("${pathProvider.projectPath}/$projectName")

        if(! projectDir.exists()) {
            throw RuntimeException("Error. Project $projectName doesn't exist")
        }

        val stageDir = File("${pathProvider.projectPath}/$projectName/$stageName")

        if(! stageDir.exists()) {
            throw RuntimeException("Error. Stage $stageName doesn't exist")
        }

        val testCases = stageDir.list()

        if(testCases.isEmpty()) {
            throw RuntimeException("Error. There are no test cases for stage $stageName")
        }

        return stageDir.list().asList()
    }

    fun getProjects() : List<String> {
        return File(pathProvider.projectPath).list().asList()
    }

    companion object {
        val INPUT_FILE_NAME = "input"
        val OUTPUT_FILE_NAME = "output"
    }
}

interface PathProv {
    val jarPath: String
    val projectPath: String
}

@Component
class PathProvider: PathProv {
    private final val pathPrefix = "/home/karolina/MGR/platform"
    final override val jarPath = "$pathPrefix/jars"
    final override val projectPath = "$pathPrefix/projects"
}