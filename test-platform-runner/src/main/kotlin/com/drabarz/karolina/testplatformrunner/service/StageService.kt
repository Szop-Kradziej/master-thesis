package com.drabarz.karolina.testplatformrunner.service

import com.drabarz.karolina.testplatformrunner.api.StageDao
import com.drabarz.karolina.testplatformrunner.model.Project
import com.drabarz.karolina.testplatformrunner.model.ProjectsRepository
import com.drabarz.karolina.testplatformrunner.model.Stage
import com.drabarz.karolina.testplatformrunner.model.StagesRepository
import com.drabarz.karolina.testplatformrunner.service.helper.DeleteFileHelper
import com.drabarz.karolina.testplatformrunner.service.helper.PathProvider
import com.drabarz.karolina.testplatformrunner.service.helper.StagePathProvider
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Component
class StageService(
        val stagePathProvider: StagePathProvider,
        val deleteFileHelper: DeleteFileHelper,
        val stagesRepository: StagesRepository,
        val projectsRepository: ProjectsRepository) {

    private final val testCaseService = TestCaseService(stagePathProvider)

    fun addStage(projectName: String, stageName: String, startDate: String?, endDate: String?): String {
        log.info("Adding stage: $stageName for project: $projectName")

        val projectDir = stagePathProvider.getProjectDir(projectName)
        if (!projectDir.exists()) {
            log.error("Can not create stage with name $stageName for project $projectName, project doesn't exist")
            throw RuntimeException("Error. Can not create stage for project. Project $projectName doesn't exist")
        }

        val stageDir = stagePathProvider.getTaskDir(projectName, stageName)

        if (stageDir.exists()) {
            log.warn("Can not create stage with name $stageName for project $projectName, stage already exist")
            throw RuntimeException("Warning. Stage already exists")
        }

        stageDir.mkdirs()

        saveStageMetadata(projectName, stageName, startDate, endDate)
        log.info("Stage with name: $stageName for project $projectName created")

        return "200"
    }

    private fun saveStageMetadata(projectName: String, stageName: String, startDate: String?, endDate: String?) =
        stagesRepository.save(
                Stage(
                        name = stageName,
                        project = projectsRepository.findByName(projectName),
                        startDate = startDate.toDate(),
                        endDate = endDate.toDate()
                ))

    fun getStages(projectName: String): List<StageDao> {
        log.info("Getting stages for project $projectName")

        val projectDir = stagePathProvider.getProjectDir(projectName)
        if (!projectDir.exists()) {
            log.error("Can not fetch stages for project $projectName, project doesn't exist")
            throw RuntimeException("Error. Can not fetch stages for project. Project $projectName doesn't exist")
        }

        val stagesDir = stagePathProvider.getTasksDir(projectName)
        if (!stagesDir.exists()) {
            log.warn("There is no stages defined for project $projectName")
            return emptyList()
        }

        return stagesDir.list().asList()
                .map {
                    val stageMetadata: Stage? = stagesRepository.findByNameAndProject_Name(it, projectName) ?: Stage(project = Project())
                    StageDao(
                            it,
                            getStageDescriptionName(projectName, it),
                            stageMetadata?.startDate.toFormattedString(),
                            stageMetadata?.endDate.toFormattedString(),
                            testCaseService.getTestCases(projectName, it).sortedBy { it.testCaseName })
                }.sortedBy { it.endDate }
    }

    private fun getStageDescriptionName(projectName: String, stageName: String): String? {
        val stageDescriptionDir = stagePathProvider.getTaskDescriptionDir(projectName, stageName)
        if (!stageDescriptionDir.exists() || stageDescriptionDir.list().size != 1) {
            log.warn("There is no stage description defined for stage: $stageName in project $projectName")
            return null
        }
        return stageDescriptionDir.list().first()
    }

    fun addStageDescription(uploadedFile: MultipartFile, projectName: String, stageName: String): String {
        log.info("Adding description for stage: $stageName in project: $projectName")

        val stageDir = stagePathProvider.getTaskDir(projectName, stageName)
        if (!stageDir.exists()) {
            log.error("Stage: $stageName for project: $projectName doesn't exist")
            throw RuntimeException("Error. Stage $stageName for project: $projectName doesn't exist")
        }

        val descriptionDir = stagePathProvider.getTaskDescriptionDir(projectName, stageName)
        descriptionDir.mkdir()

        deleteFileHelper.deleteSingleFileFromDir(descriptionDir)

        val outputFile = File(descriptionDir.path, uploadedFile.originalFilename)
        uploadedFile.transferTo(outputFile)

        log.info("Description file for stage: $stageName in project $projectName saved")

        return "200"
    }

    fun getStageDescription(projectName: String, stageName: String): File {
        log.info("Getting description for stage: $stageName in project: $projectName")
        val stageDescriptionDir = stagePathProvider.getTaskDescriptionDir(projectName, stageName)
        if (stageDescriptionDir.exists() && stageDescriptionDir.list().size == 1) {
            return stageDescriptionDir.listFiles().first()
        }
        log.warn("Can not get description file for stage: $stageName and project: $projectName. File doesn't exist")
        throw java.lang.RuntimeException("Error file doesn't exist")
    }

    fun deleteStage(projectName: String, stageName: String): String {
        log.info("Deleting stage: $stageName for project $projectName")

        try {
            stagesRepository.findByNameAndProject_Name(stageName, projectName)
                    ?.let { stagesRepository.delete(it) }
        } catch (e: Exception) {
            log.warn("Can not delete stage $stageName for project $projectName, stage is already assigned to an integration")
            throw java.lang.RuntimeException("Error. Can not remove stage $stageName, stage is already assigned to an integration")
        }

        val stageDir = stagePathProvider.getTaskDir(projectName, stageName)
        if (stageDir.exists()) {
            if (stageDir.list().isNotEmpty()) {
                testCaseService.deleteTestCases(projectName, stageName)
                deleteStageDescriptionDir(projectName, stageName)
            }
            stageDir.delete()
        }

        log.info("Stage: $stageName for project: $projectName deleted")

        return "200"
    }


    private fun deleteStageDescriptionDir(projectName: String, stageName: String) {
        val stageDir = stagePathProvider.getTaskDir(projectName, stageName)

        if (stageDir.list().contains(PathProvider.DESCRIPTION)) {
            val descriptionDir = stagePathProvider.getTaskDescriptionDir(projectName, stageName)
            deleteFileHelper.deleteSingleFileWithDirectory(descriptionDir)
        }

        log.info("Description directory for stage: $stageName in project: $projectName deleted")
    }

    fun editStageDate(projectName: String, stageName: String, date: String?, type: String): String {
        log.info("Editing ${type.toLowerCase()} date for stage: $stageName in project: $projectName")

        stagesRepository.findByNameAndProject_Name(stageName, projectName)
                .also { changeDate(it, date, type) }
                ?.let { stagesRepository.save(it) }
                .also { log.info("Stage ${type.toLowerCase()} date for stage: $stageName in projectL $projectName edited") }

        return "200"
    }
    private fun changeDate(it: Stage?, date: String?, type: String) {
        if (type == "START") {
            it?.startDate = date.toDate()
        } else {
            it?.endDate = date.toDate()
        }
    }

    fun editTestCaseParameters(projectName: String, stageName: String, testCaseName: String, parameters: String?): String {
        return testCaseService.editParameters(projectName, stageName, testCaseName, parameters)
    }

    fun saveTestCase(inputFile: MultipartFile, outputFile: MultipartFile, projectName: String, stageName: String, testCaseName: String): String {
        return testCaseService.saveTestCase(inputFile, outputFile, projectName, stageName, testCaseName)
    }

    fun getTestCaseFile(projectName: String, stageName: String, testCaseName: String, fileType: String): File {
        return testCaseService.getTestCaseFile(projectName, stageName, testCaseName, fileType)
    }

    fun uploadTestCaseFile(projectName: String, stageName: String, testCaseName: String, fileType: String, file: MultipartFile): String {
        return testCaseService.uploadTestCaseFile(projectName, stageName, testCaseName, fileType, file)
    }

    fun deleteTestCase(projectName: String, stageName: String, testCaseName: String): String {
        return testCaseService.deleteTestCase(projectName, stageName, testCaseName)
    }

    companion object {
        val log = LoggerFactory.getLogger(StageService::class.java);
    }
}

private fun Date?.toFormattedString(): String? {
    if (this == null) {
        return null
    }

    return SimpleDateFormat("yyyy-MM-dd").format(this)
}

fun String?.toDate(): Date? {
    if (this.isNullOrBlank()) {
        return null
    }

    return SimpleDateFormat("yyyy-MM-dd").parse(this);
}
