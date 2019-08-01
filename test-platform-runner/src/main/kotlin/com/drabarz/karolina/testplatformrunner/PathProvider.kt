package com.drabarz.karolina.testplatformrunner

import org.springframework.stereotype.Component
import java.io.File


interface PathProv {
    val jarPath: String
    val projectsPath: String
}

@Component
class PathProvider : PathProv {
    private final val pathPrefix = "/home/karolina/MGR/platform"
    final override val jarPath = "$pathPrefix/jars"
    final override val projectsPath = "$pathPrefix/projects"

    fun getProjectDir(projectName: String): File {
        return File("$projectsPath/$projectName")
    }

    fun getProjectDescriptionDir(projectName: String): File {
        return File(getProjectDir(projectName), DESCRIPTION)
    }

    fun getProjectEnvironmentDir(projectName: String): File {
        return File(getProjectDir(projectName), ENVIRONMENT)
    }

    fun getStudentStageDir(projectName: String, stageName: String): File {
        return File("$jarPath/$projectName/$stageName")
    }

    fun getStudentResultsDir(projectName: String, stageName: String): File {
        return getStudentFileDir(projectName, stageName, RESULTS)
    }

    fun getStudentCodeDir(projectName: String, stageName: String): File {
        return getStudentFileDir(projectName, stageName, CODE)
    }

    fun getStudentReportDir(projectName: String, stageName: String): File {
        return getStudentFileDir(projectName, stageName, REPORT)
    }

    fun getStudentBinDir(projectName: String, stageName: String): File {
        return getStudentFileDir(projectName, stageName, BIN)
    }

    fun getStudentOutputDir(projectName: String, stageName: String): File {
        return getStudentFileDir(projectName, stageName, OUTPUT)
    }

    fun getStudentLogsDir(projectName: String, stageName: String): File {
        return getStudentFileDir(projectName, stageName, LOGS)
    }

    fun getStudentLogsFileDir(projectName: String, stageName: String, testCaseName: String): File {
        return File(getStudentLogsDir(projectName, stageName), testCaseName)
    }

    fun getStudentFileDir(projectName: String, stageName: String, file: String): File {
        return File(getStudentStageDir(projectName, stageName), file)
    }

    companion object {
        const val STAGES = "stages"
        const val INTEGRATIONS = "integrations"
        const val DESCRIPTION = "description"
        const val ENVIRONMENT = "environment"
        const val BIN = "bin"
        const val REPORT = "report"
        const val CODE = "code"
        const val RESULTS = "results"
        const val LOGS = "logs"
        const val OUTPUT = "output"
        const val TEST_CASES = "test_cases"
        const val PARAMETERS = "parameters"
    }
}


interface TaskPathProvider {

    fun getProjectDir(projectName: String): File

    fun getTasksDir(projectName: String): File

    fun getTaskDir(projectName: String, taskName: String): File {
        return File(getTasksDir(projectName), taskName)
    }

    fun getTaskTestCasesDir(projectName: String, taskName: String): File {
        return File(getTaskDir(projectName, taskName), PathProvider.TEST_CASES)
    }

    fun getTaskTestCaseDir(projectName: String, taskName: String, testCaseName: String): File {
        return File(getTaskTestCasesDir(projectName, taskName), testCaseName)
    }

    fun getTaskTestCaseFileDir(projectName: String, taskName: String, testCaseName: String, fileName: String): File {
        return File(getTaskTestCaseDir(projectName, taskName, testCaseName), fileName)
    }

    fun getTaskTestCaseParametersDir(projectName: String, taskName: String, testCaseName: String): File {
        return File(getTaskTestCaseDir(projectName, taskName, testCaseName), PathProvider.PARAMETERS)
    }

    fun getTaskDescriptionDir(projectName: String, taskName: String): File {
        return File(getTaskDir(projectName, taskName), PathProvider.DESCRIPTION)
    }
}

@Component
class StagePathProvider : PathProvider(), TaskPathProvider {

    override fun getTasksDir(projectName: String): File {
        return File(getProjectDir(projectName), STAGES)
    }
}

@Component
class IntegrationPathProvider : PathProvider(), TaskPathProvider {

    override fun getTasksDir(projectName: String): File {
        return File(getProjectDir(projectName), INTEGRATIONS)
    }
}