package com.drabarz.karolina.testplatformrunner.service

import com.drabarz.karolina.testplatformrunner.model.GroupsRepository
import com.drabarz.karolina.testplatformrunner.model.Project
import com.drabarz.karolina.testplatformrunner.model.ProjectsRepository
import com.drabarz.karolina.testplatformrunner.service.helper.DeleteFileHelper
import com.drabarz.karolina.testplatformrunner.service.helper.PathProvider
import com.drabarz.karolina.testplatformrunner.service.helper.StagePathProvider
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File
import javax.transaction.Transactional

@Component
class ProjectService(
        val pathProvider: StagePathProvider,
        val stageService: StageService,
        val projectsRepository: ProjectsRepository,
        val groupsRepository: GroupsRepository,
        val groupService: GroupService,
        val deleteFileHelper: DeleteFileHelper) {

    fun addProject(projectName: String): String {
        val projectDir = pathProvider.getProjectDir(projectName)

        if (projectDir.exists()) {
            throw RuntimeException("Warning. Project already exists")
        }

        projectDir.mkdir()
        projectsRepository.save(Project(name = projectName))

        return "200"
    }

    fun getProjects(): List<String> {
        return File(pathProvider.projectsPath).list().asList().sortedBy { it }
    }

    fun addProjectDescription(uploadedFile: MultipartFile, projectName: String): String {
        addProjectFile(uploadedFile, projectName, pathProvider.getProjectDescriptionDir(projectName))

        return "200"
    }

    fun addProjectEnvironment(uploadedFile: MultipartFile, projectName: String): String {
        addProjectFile(uploadedFile, projectName, pathProvider.getProjectEnvironmentDir(projectName))

        return "200"
    }

    fun addProjectFile(uploadedFile: MultipartFile, projectName: String, fileDir: File) {
        val projectDir = pathProvider.getProjectDir(projectName)
        if (!projectDir.exists()) {
            throw RuntimeException("Error. Project $projectName doesn't exist")
        }

        fileDir.mkdir()

        deleteFileHelper.deleteSingleFileFromDir(fileDir)

        val outputFile = File(fileDir.path, uploadedFile.originalFilename)
        uploadedFile.transferTo(outputFile)
    }

    fun getProjectDescriptionName(projectName: String): String? {
        return getProjectFileName(pathProvider.getProjectDescriptionDir(projectName))
    }

    fun getProjectEnvironmentName(projectName: String): String? {
        return getProjectFileName(pathProvider.getProjectEnvironmentDir(projectName))
    }

    fun getProjectFileName(fileDir: File): String? {
        if (!fileDir.exists() || fileDir.list().size != 1) {
            return null
        }
        return fileDir.list().first()
    }

    fun getProjectDescription(projectName: String): File {
        return getProjectFile(pathProvider.getProjectDescriptionDir(projectName))
    }

    fun getProjectEnvironment(projectName: String): File {
       return getProjectFile(pathProvider.getProjectEnvironmentDir(projectName))
    }

    fun getProjectFile(fileDir: File): File {
        if (fileDir.exists() && fileDir.list().size == 1) {
            return fileDir.listFiles().first()
        }

        throw java.lang.RuntimeException("Error file doesn't exist")
    }

    @Transactional
    fun deleteProject(projectName: String): String {
        val projectDir = pathProvider.getProjectDir(projectName)

        if (projectDir.exists()) {
            if (projectDir.list().isNotEmpty()) {
                deleteStages(projectName)
                deleteProjectDescriptionDir(projectName)
            }
            projectDir.delete()
        }

        groupsRepository.findAllByProject_Name(projectName)
                .let { it.forEach { groupService.deleteGroup(it.name, projectName) } }

        projectsRepository.deleteByName(projectName)

        return "200"
    }

    private fun deleteStages(projectName: String) {
        val stagesDir = pathProvider.getTasksDir(projectName)
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
            deleteFileHelper.deleteSingleFileWithDirectory(descriptionDir)
        }
    }

    companion object {
        val log = LoggerFactory.getLogger(ProjectService::class.java);
    }
}
