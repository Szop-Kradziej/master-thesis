package com.drabarz.karolina.testplatformrunner

import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.lang.RuntimeException

@Component
class TestCaseService {
    fun saveTestCase(inputFile: MultipartFile, outputFile: MultipartFile, stageName: String, testCaseName: String): TestResponse {
        val testCasePath = "$PATH_PREFIX/$stageName/$testCaseName"

        val stageDir = File("$PATH_PREFIX/$stageName")
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

    fun addStage(stageName: String): TestResponse {
        val dir = File("$PATH_PREFIX/$stageName")

        if(dir.exists()) {
            return Error("Warning. Stage already exists")
        }

        dir.mkdir()

        return Success()
    }

    fun getTestCaseName(stageName: String): String {
        val dir = File("$PATH_PREFIX/$stageName")

        if(! dir.exists()) {
            throw RuntimeException("Error. Stage $stageName doesn't exist")
        }

        val testCases = dir.list()

        if(testCases.isEmpty()) {
            throw RuntimeException("Error. There are no test cases for stage $stageName")
        }

        return dir.list().get(0)
    }

    companion object {
        val PATH_PREFIX = "/home/karolina/MGR/tests"
        val INPUT_FILE_NAME = "input"
        val OUTPUT_FILE_NAME = "output"
    }
}