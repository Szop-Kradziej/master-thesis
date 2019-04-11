package com.drabarz.karolina.testplatformrunner

import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.lang.RuntimeException

@Component
class TestCaseService {

    val pathProvider: PathProv = PathProvider()

    fun addProject(projectName: String): String {
        val dir = File("${pathProvider.projectPath}/$projectName")

        if(dir.exists()) {
            throw RuntimeException("Warning. Project already exists")
        }

        dir.mkdir()

        return "200"
    }

    fun addStage(projectName: String, stageName: String): String {
        val stageDir = File("${pathProvider.projectPath}/$projectName")
        if(! stageDir.exists()) {
            throw RuntimeException("Error. Can not create stage for project. Project $projectName doesn't exist")
        }

        val dir = File("${pathProvider.projectPath}/$projectName/$stageName")

        if(dir.exists()) {
            throw RuntimeException("Warning. Stage already exists")
        }

        dir.mkdir()

        return "200"
    }

    fun saveTestCase(inputFile: MultipartFile, outputFile: MultipartFile, projectName: String, stageName: String, testCaseName: String): String {
        val testCasePath = "${pathProvider.projectPath}/$projectName/$stageName/$testCaseName"

        val projectDir = File("${pathProvider.projectPath}/$projectName")
        if(! projectDir.exists()) {
            throw RuntimeException("Error. Can not create test case for project. Project $projectName doesn't exist")
        }

        val stageDir = File("${pathProvider.projectPath}/$projectName/$stageName")
        if(! stageDir.exists()) {
            throw RuntimeException("Error. Can not create test case for stage. Stage $stageName doesn't exist")
        }

        val testCaseDir = File(testCasePath)
        testCaseDir.mkdirs()

        val savedInputFile = File(testCasePath, INPUT_FILE_NAME)
        inputFile.transferTo(savedInputFile)

        val savedOutputFile = File(testCasePath, OUTPUT_FILE_NAME)
        outputFile.transferTo(savedOutputFile)

        return "200"
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

        return stageDir.list().asList()
    }

    fun getProjects() : List<String> {
        return File(pathProvider.projectPath).list().asList()
    }

    fun getStages(projectName: String): List<Stage> {
        val projectDir = File("${pathProvider.projectPath}/$projectName")
        if(! projectDir.exists()) {
            throw RuntimeException("Error. Can not get stages for project. Project $projectName doesn't exist")
        }

        return projectDir.list().asList().map{ it -> Stage(it, getTestCasesNames(projectName, it))}
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