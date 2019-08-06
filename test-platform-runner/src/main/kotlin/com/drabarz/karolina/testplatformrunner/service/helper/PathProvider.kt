package com.drabarz.karolina.testplatformrunner.service.helper

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

    fun getStudentResultsDir(projectName: String, stageName: String): File {
        return getStudentFileDir(projectName, stageName, PathProvider.RESULTS)
    }

    fun getStudentFileDir(projectName: String, stageName: String, file: String): File {
        return File(getStudentTaskDir(projectName, stageName), file)
    }

    fun getStudentTaskDir(projectName: String, taskName: String): File

    fun getStudentCodeDir(projectName: String, stageName: String): File {
        return getStudentFileDir(projectName, stageName, PathProvider.CODE)
    }

    fun getStudentReportDir(projectName: String, stageName: String): File {
        return getStudentFileDir(projectName, stageName, PathProvider.REPORT)
    }

    fun getStudentBinDir(projectName: String, stageName: String): File {
        return getStudentFileDir(projectName, stageName, PathProvider.BIN)
    }

    fun getStudentOutputDir(projectName: String, stageName: String): File {
        return getStudentFileDir(projectName, stageName, PathProvider.OUTPUT)
    }

    fun getStudentLogsDir(projectName: String, stageName: String): File {
        return getStudentFileDir(projectName, stageName, PathProvider.LOGS)
    }

    fun getStudentLogsFileDir(projectName: String, stageName: String, testCaseName: String): File {
        return File(getStudentLogsDir(projectName, stageName), testCaseName)
    }
}

@Component
class StagePathProvider : PathProvider(), TaskPathProvider {

    override fun getTasksDir(projectName: String): File {
        return File(getProjectDir(projectName), STAGES)
    }

    override fun getStudentTaskDir(projectName: String, taskName: String): File {
        return File("$jarPath/$projectName/$STAGES/$taskName")
    }
}

@Component
class IntegrationPathProvider : PathProvider(), TaskPathProvider {

    override fun getTasksDir(projectName: String): File {
        return File(getProjectDir(projectName), INTEGRATIONS)
    }

    override fun getStudentTaskDir(projectName: String, taskName: String): File {
        return File("$jarPath/$projectName/$INTEGRATIONS/$taskName")
    }
}