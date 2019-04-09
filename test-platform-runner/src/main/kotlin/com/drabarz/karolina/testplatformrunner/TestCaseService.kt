package com.drabarz.karolina.testplatformrunner

import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.util.*

@Component
class TestCaseService {
    fun saveTestCase(inputFile: MultipartFile, outputFile: MultipartFile) {
        val testCaseName = UUID.randomUUID()
        val testCasePath = "$PATH_PREFIX/$testCaseName"

        val dir = File(testCasePath)
        dir.mkdir()

        val savedInputFile = File(testCasePath, INPUT_FILE_NAME)
        inputFile.transferTo(savedInputFile)

        val savedOutputFile = File(testCasePath, OUTPUT_FILE_NAME)
        outputFile.transferTo(savedOutputFile)
    }

    companion object {
        val PATH_PREFIX = "/home/karolina/MGR/tests"
        val INPUT_FILE_NAME = "input"
        val OUTPUT_FILE_NAME = "output"
    }
}