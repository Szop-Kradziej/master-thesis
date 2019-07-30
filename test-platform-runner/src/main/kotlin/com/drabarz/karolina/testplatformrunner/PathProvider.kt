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

    fun getIntegrationsDir(projectName: String): File {
        return File(getProjectDir(projectName), INTEGRATIONS)
    }

    fun getIntegrationDir(projectName: String, integrationName: String): File {
        return File(getIntegrationsDir(projectName), integrationName)
    }

    fun getIntegrationTestCasesDir(projectName: String, integrationName: String): File {
        return File(getIntegrationDir(projectName, integrationName), TEST_CASES)
    }

    fun getIntegrationTestCaseDir(projectName: String, integrationName: String, testCaseName: String): File {
        return File(getIntegrationTestCasesDir(projectName, integrationName), testCaseName)
    }

    fun getIntegrationTestCaseFileDir(projectName: String, integrationName: String, testCaseName: String, fileName: String): File {
        return File(getIntegrationTestCaseDir(projectName, integrationName, testCaseName), fileName)
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
        const val TEST_CASES = "test_cases"
        const val PARAMETERS = "parameters"
    }
}


interface TaskPathProvider {

    fun getProjectDir(projectName: String): File

    fun getTasksDir(projectName: String): File

    fun getTaskDir(projectName: String, stageName: String): File

    fun getTaskTestCasesDir(projectName: String, stageName: String): File

    fun getTaskTestCaseDir(projectName: String, stageName: String, testCaseName: String): File

    fun getTaskTestCaseFileDir(projectName: String, stageName: String, testCaseName: String, fileName: String): File

    fun getTaskTestCaseParametersDir(projectName: String, stageName: String, testCaseName: String): File

    fun getTaskDescriptionDir(projectName: String, stageName: String): File
}

@Component
class StagePathProvider : PathProvider(), TaskPathProvider {

    override fun getTasksDir(projectName: String): File {
        return File(getProjectDir(projectName), STAGES)
    }

    override fun getTaskDir(projectName: String, stageName: String): File {
        return File(getTasksDir(projectName), stageName)
    }

    override fun getTaskTestCasesDir(projectName: String, stageName: String): File {
        return File(getTaskDir(projectName, stageName), TEST_CASES)
    }

    override fun getTaskTestCaseDir(projectName: String, stageName: String, testCaseName: String): File {
        return File(getTaskTestCasesDir(projectName, stageName), testCaseName)
    }

    override fun getTaskTestCaseFileDir(projectName: String, stageName: String, testCaseName: String, fileName: String): File {
        return File(getTaskTestCaseDir(projectName, stageName, testCaseName), fileName)
    }

    override fun getTaskTestCaseParametersDir(projectName: String, stageName: String, testCaseName: String): File {
        return File(getTaskTestCaseDir(projectName, stageName, testCaseName), PARAMETERS)
    }

    override fun getTaskDescriptionDir(projectName: String, stageName: String): File {
        return File(getTaskDir(projectName, stageName), DESCRIPTION)
    }
}