package com.drabarz.karolina.testplatformrunner

import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File

@Component
class ProjectService(val pathProvider: PathProvider, val testCaseService: TestCaseService) {

    fun addProject(projectName: String): String {
        val projectDir = pathProvider.getProjectDir(projectName)

        if (projectDir.exists()) {
            throw RuntimeException("Warning. Project already exists")
        }

        projectDir.mkdir()

        return "200"
    }

    fun addStage(projectName: String, stageName: String): String {
        val projectDir = pathProvider.getProjectDir(projectName)
        if (!projectDir.exists()) {
            throw RuntimeException("Error. Can not create stage for project. Project $projectName doesn't exist")
        }

        val stageDir = pathProvider.getStageDir(projectName, stageName)

        if (stageDir.exists()) {
            throw RuntimeException("Warning. Stage already exists")
        }

        stageDir.mkdir()

        return "200"
    }

    fun getProjects(): List<String> {
        return File(pathProvider.projectPath).list().asList()
    }

    fun getStages(projectName: String): List<Stage> {
        val projectDir = pathProvider.getProjectDir(projectName)
        if (!projectDir.exists()) {
            throw RuntimeException("Error. Can not get stages for project. Project $projectName doesn't exist")
        }

        return projectDir.list().asList().map { it -> Stage(it, testCaseService.getTestCasesNames(projectName, it)) }
    }

    fun addProjectDescription(uploadedFile: MultipartFile, projectName: String): String {
        val projectDir = pathProvider.getProjectDir(projectName)
        if (!projectDir.exists()) {
            throw RuntimeException("Error. Project $projectName doesn't not exist")
        }

        val descriptionDir = pathProvider.getProjectDescriptionDir(projectName)
        descriptionDir.mkdir()

        if(descriptionDir.list().isNotEmpty()) {
            JarService.log.info("Existing file to delete: " + descriptionDir.list()[0] + " from: " + descriptionDir.absolutePath)
        }

        val outputFile = File(descriptionDir.path, uploadedFile.originalFilename)
        uploadedFile.transferTo(outputFile)

        return "200"
    }

    fun getProjectDescriptionName(projectName: String): String? {
        val projectDescriptionDir = pathProvider.getProjectDescriptionDir(projectName)
        if (!projectDescriptionDir.exists() || projectDescriptionDir.list().size != 1) {
            return null
        }
        return projectDescriptionDir.list()[0]
    }

    fun getProjectDescription(projectName: String): File {
        val projectDescriptionDir = pathProvider.getProjectDescriptionDir(projectName)
        if (projectDescriptionDir.exists() && projectDescriptionDir.list().size == 1) {
            return File(projectDescriptionDir, projectDescriptionDir.list()[0])
        }

        throw java.lang.RuntimeException("Error file doesn't exist")
    }
}