package com.drabarz.karolina.testplatformrunner

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File

@Component
class ProjectService(
        val pathProvider: PathProvider,
        val stageService: StageService,
        val deleteFileService: DeleteFileService) {

    fun addProject(projectName: String): String {
        val projectDir = pathProvider.getProjectDir(projectName)

        if (projectDir.exists()) {
            throw RuntimeException("Warning. Project already exists")
        }

        projectDir.mkdir()

        return "200"
    }

    fun getProjects(): List<String> {
        return File(pathProvider.projectPath).list().asList()
    }

    fun addProjectDescription(uploadedFile: MultipartFile, projectName: String): String {
        val projectDir = pathProvider.getProjectDir(projectName)
        if (!projectDir.exists()) {
            throw RuntimeException("Error. Project $projectName doesn't not exist")
        }

        val descriptionDir = pathProvider.getProjectDescriptionDir(projectName)
        descriptionDir.mkdir()

        deleteFileService.deleteSingleFileFromDir(descriptionDir)

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

    fun deleteProject(projectName: String): String {
        val projectDir = pathProvider.getProjectDir(projectName)

        if (projectDir.exists()) {
            if (projectDir.list().isNotEmpty()) {
                deleteStages(projectName)
                deleteProjectDescriptionDir(projectName)
            }
            projectDir.delete()
        }

        return "200"
    }

    private fun deleteStages(projectName: String) {
        val stagesDir = pathProvider.getStagesDir(projectName)
        if (!stagesDir.exists()) {
            return
        }

        stagesDir.list()
                .forEach { stageService.deleteStage(projectName, it) }

        stagesDir.delete()
    }

    private fun deleteProjectDescriptionDir(projectName: String) {
        val projectDir = pathProvider.getProjectDir(projectName)

        if (projectDir.list().contains(PathProvider.DESCRIPTION)) {
            val descriptionDir = pathProvider.getProjectDescriptionDir(projectName)
            deleteFileService.deleteSingleFileWithDirectory(descriptionDir)
        }
    }

    companion object {
        val log = LoggerFactory.getLogger(ProjectService::class.java);
    }
}
