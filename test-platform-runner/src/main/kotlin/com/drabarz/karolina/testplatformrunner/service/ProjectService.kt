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

    fun getProjects(): List<String> {
        log.info("Getting all projects")

        return File(pathProvider.projectsPath).list().asList().sortedBy { it }
    }

    fun addProject(projectName: String): String {
        log.info("Adding project: $projectName")

        val projectDir = pathProvider.getProjectDir(projectName)

        if (projectDir.exists()) {
            log.warn("Can not add project: $projectName, project already exist")
            throw RuntimeException("Warning. Project already exists")
        }

        projectDir.mkdir()
        projectsRepository.save(Project(name = projectName))

        log.info("Project: $projectName added")

        return SUCCESS_RESPONSE
    }

    fun addProjectDescription(uploadedFile: MultipartFile, projectName: String): String {
        log.info("Adding description for project: $projectName")

        addProjectFile(uploadedFile, projectName, pathProvider.getProjectDescriptionDir(projectName))

        log.info("Description for project: $projectName added")

        return SUCCESS_RESPONSE
    }

    fun addProjectEnvironment(uploadedFile: MultipartFile, projectName: String): String {
        log.info("Adding environment for project: $projectName")

        addProjectFile(uploadedFile, projectName, pathProvider.getProjectEnvironmentDir(projectName))

        log.info("Environment for project: $projectName added")

        return SUCCESS_RESPONSE
    }

    private fun addProjectFile(uploadedFile: MultipartFile, projectName: String, fileDir: File) {
        val projectDir = pathProvider.getProjectDir(projectName)
        if (!projectDir.exists()) {
            log.error("Can not add file for project $projectName, project doesn't exist")
            throw RuntimeException("Error. Project $projectName doesn't exist")
        }

        fileDir.mkdir()

        deleteFileHelper.deleteSingleFileFromDir(fileDir)

        val outputFile = File(fileDir.path, uploadedFile.originalFilename)
        uploadedFile.transferTo(outputFile)
    }

    fun getProjectDescriptionName(projectName: String): String? {
        log.info("Getting description name for project: $projectName")

        return getProjectFileName(pathProvider.getProjectDescriptionDir(projectName))
    }

    fun getProjectEnvironmentName(projectName: String): String? {
        log.info("Getting environment name for project: $projectName")

        return getProjectFileName(pathProvider.getProjectEnvironmentDir(projectName))
    }

    private fun getProjectFileName(fileDir: File): String? {
        if (!fileDir.exists() || fileDir.list().size != 1) {
            return null
        }
        return fileDir.list().first()
    }

    fun getProjectDescription(projectName: String): File {
        log.info("Getting description for project: $projectName")

        return getProjectFile(pathProvider.getProjectDescriptionDir(projectName))
    }

    fun getProjectEnvironment(projectName: String): File {
        log.info("Getting environment for project: $projectName")

        return getProjectFile(pathProvider.getProjectEnvironmentDir(projectName))
    }

    private fun getProjectFile(fileDir: File): File {
        if (fileDir.exists() && fileDir.list().size == 1) {
            return fileDir.listFiles().first()
        }

        throw java.lang.RuntimeException("Error file doesn't exist")
    }

    @Transactional
    fun deleteProject(projectName: String): String {
        log.info("Deleting project: $projectName")

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

        log.info("Project: $projectName deleted")

        return SUCCESS_RESPONSE
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
        const val SUCCESS_RESPONSE = "200"
    }
}
