package com.drabarz.karolina.testplatformrunner

import com.drabarz.karolina.testplatformrunner.model.GroupsRepository
import com.drabarz.karolina.testplatformrunner.model.Project
import com.drabarz.karolina.testplatformrunner.model.ProjectsRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File
import javax.transaction.Transactional

@Component
class ProjectService(
        val pathProvider: PathProvider,
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
        val projectDir = pathProvider.getProjectDir(projectName)
        if (!projectDir.exists()) {
            throw RuntimeException("Error. Project $projectName doesn't exist")
        }

        val descriptionDir = pathProvider.getProjectDescriptionDir(projectName)
        descriptionDir.mkdir()

        deleteFileHelper.deleteSingleFileFromDir(descriptionDir)

        val outputFile = File(descriptionDir.path, uploadedFile.originalFilename)
        uploadedFile.transferTo(outputFile)

        return "200"
    }

    fun getProjectDescriptionName(projectName: String): String? {
        val projectDescriptionDir = pathProvider.getProjectDescriptionDir(projectName)
        if (!projectDescriptionDir.exists() || projectDescriptionDir.list().size != 1) {
            return null
        }
        return projectDescriptionDir.list().first()
    }

    fun getProjectDescription(projectName: String): File {
        val projectDescriptionDir = pathProvider.getProjectDescriptionDir(projectName)
        if (projectDescriptionDir.exists() && projectDescriptionDir.list().size == 1) {
            return projectDescriptionDir.listFiles().first()
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
            deleteFileHelper.deleteSingleFileWithDirectory(descriptionDir)
        }
    }

    companion object {
        val log = LoggerFactory.getLogger(ProjectService::class.java);
    }
}
