package com.drabarz.karolina.testplatformrunner

import org.springframework.stereotype.Component
import java.io.File


interface PathProv {
    val jarPath: String
    val projectPath: String
}

@Component
class PathProvider : PathProv {
    private final val pathPrefix = "/home/karolina/MGR/platform"
    final override val jarPath = "$pathPrefix/jars"
    final override val projectPath = "$pathPrefix/projects"

    fun getProjectDir(projectName: String): File {
        return File("$projectPath/$projectName")
    }

    fun getStageDir(projectName: String, stageName: String): File {
        return File(getProjectDir(projectName), stageName)
    }

    fun getTestCasesDir(projectName: String, stageName: String): File {
        return File(getStageDir(projectName, stageName), TEST_CASES)
    }

    fun getTestCaseDir(projectName: String, stageName: String, testCaseName: String): File {
        return File(getTestCasesDir(projectName, stageName), testCaseName)
    }

    fun getTestCaseFileDir(projectName: String, stageName: String, testCaseName: String, fileName: String): File {
        return File(getTestCaseDir(projectName, stageName, testCaseName), fileName)
    }

    fun getProjectDescriptionDir(projectName: String): File {
        return File(getProjectDir(projectName), DESCRIPTION)
    }

    fun getStageDescriptionDir(projectName: String, stageName: String): File {
        return File(getStageDir(projectName, stageName), DESCRIPTION)
    }

    fun getStudentStageDir(projectName: String, stageName: String): File {
        return File("$jarPath/$projectName/$stageName")
    }

    fun getStudentResultsDir(projectName: String, stageName: String): File {
        return  getStudentFileDir(projectName, stageName, RESULTS)
    }

    fun getStudentReportDir(projectName: String, stageName: String): File {
        return  getStudentFileDir(projectName, stageName, REPORT)
    }

    fun getStudentBinDir(projectName: String, stageName: String): File {
        return  getStudentFileDir(projectName, stageName, BIN)
    }

    fun getStudentFileDir(projectName: String, stageName: String, file: String) : File {
        return File(getStudentStageDir(projectName, stageName), file)
    }

    companion object {
        const val STAGES = "stages"
        const val DESCRIPTION = "description"
        const val BIN = "bin"
        const val REPORT = "report"
        const val RESULTS = "results"
        const val TEST_CASES = "test_cases"
    }
}