package com.drabarz.karolina.testplatformrunner

import com.drabarz.karolina.testplatformrunner.model.Project
import com.drabarz.karolina.testplatformrunner.model.ProjectsRepository
import com.drabarz.karolina.testplatformrunner.model.Stage
import com.drabarz.karolina.testplatformrunner.model.StagesRepository
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Component
class StageService(
        val pathProvider: StagePathProvider,
        val deleteFileHelper: DeleteFileHelper,
        val stagesRepository: StagesRepository,
        val projectsRepository: ProjectsRepository) {

    private final val testCaseService = TestCaseService(pathProvider)

    fun addStage(projectName: String, stageName: String, startDate: String?, endDate: String?, pointsNumber: String?): String {
        val projectDir = pathProvider.getProjectDir(projectName)
        if (!projectDir.exists()) {
            throw RuntimeException("Error. Can not create stage for project. Project $projectName doesn't exist")
        }

        val stageDir = pathProvider.getTaskDir(projectName, stageName)

        if (stageDir.exists()) {
            throw RuntimeException("Warning. StageDao already exists")
        }

        stageDir.mkdirs()

        saveStageMetadata(projectName, stageName, startDate, endDate, pointsNumber)

        return "200"
    }

    private fun saveStageMetadata(projectName: String, stageName: String, startDate: String?, endDate: String?, pointsNumber: String?) {
        stagesRepository.save(
                Stage(
                        name = stageName,
                        project = projectsRepository.findByName(projectName),
                        startDate = startDate.toDate(),
                        endDate = endDate.toDate(),
                        pointsNumber = pointsNumber?.toIntOrNull()
                ))
    }

    fun getStages(projectName: String): List<StageDao> {
        val projectDir = pathProvider.getProjectDir(projectName)
        if (!projectDir.exists()) {
            throw RuntimeException("Error. Can not get stages for project. Project $projectName doesn't exist")
        }
        val stagesDir = pathProvider.getTasksDir(projectName)
        if (!stagesDir.exists()) {
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
                            stageMetadata?.pointsNumber?.toString(),
                            testCaseService.getTestCases(projectName, it).sortedBy { it.testCaseName })
                }.sortedBy { it.endDate }
    }

    fun addStageDescription(uploadedFile: MultipartFile, projectName: String, stageName: String): String {
        val stageDir = pathProvider.getTaskDir(projectName, stageName)
        if (!stageDir.exists()) {
            throw RuntimeException("Error. Stage $stageName for project: $projectName doesn't exist")
        }

        val descriptionDir = pathProvider.getTaskDescriptionDir(projectName, stageName)
        descriptionDir.mkdir()

        deleteFileHelper.deleteSingleFileFromDir(descriptionDir)

        val outputFile = File(descriptionDir.path, uploadedFile.originalFilename)
        uploadedFile.transferTo(outputFile)

        return "200"
    }

    fun getStageDescriptionName(projectName: String, stageName: String): String? {
        val stageDescriptionDir = pathProvider.getTaskDescriptionDir(projectName, stageName)
        if (!stageDescriptionDir.exists() || stageDescriptionDir.list().size != 1) {
            return null
        }
        return stageDescriptionDir.list().first()
    }


    fun getStageDescription(projectName: String, stageName: String): File {
        val stageDescriptionDir = pathProvider.getTaskDescriptionDir(projectName, stageName)
        if (stageDescriptionDir.exists() && stageDescriptionDir.list().size == 1) {
            return stageDescriptionDir.listFiles().first()
        }

        throw java.lang.RuntimeException("Error file doesn't exist")
    }

    fun deleteStage(projectName: String, stageName: String): String {
        val stageDir = pathProvider.getTaskDir(projectName, stageName)

        if (stageDir.exists()) {
            if (stageDir.list().isNotEmpty()) {
                testCaseService.deleteTestCases(projectName, stageName)
                deleteStageDescriptionDir(projectName, stageName)
            }
            stageDir.delete()
        }

        stagesRepository.findByNameAndProject_Name(stageName, projectName)
                ?.let { stagesRepository.delete(it) }

        return "200"
    }


    private fun deleteStageDescriptionDir(projectName: String, stageName: String) {
        val stageDir = pathProvider.getTaskDir(projectName, stageName)

        if (stageDir.list().contains(PathProvider.DESCRIPTION)) {
            val descriptionDir = pathProvider.getTaskDescriptionDir(projectName, stageName)
            deleteFileHelper.deleteSingleFileWithDirectory(descriptionDir)
        }
    }

    fun editStageDate(projectName: String, stageName: String, date: String?, type: String) =
            stagesRepository.findByNameAndProject_Name(stageName, projectName)
                    .also { changeDate(it, date, type) }
                    ?.let { stagesRepository.save(it) }
                    .let { "200" }

    private fun changeDate(it: Stage?, date: String?, type: String) {
        if (type == "START") {
            it?.startDate = date.toDate()
        } else {
            it?.endDate = date.toDate()
        }
    }

    fun editStagePointsNumber(projectName: String, stageName: String, pointsNumber: String?) =
            stagesRepository.findByNameAndProject_Name(stageName, projectName)
                    .also { it?.pointsNumber = pointsNumber?.toIntOrNull() }
                    ?.let { stagesRepository.save(it) }
                    .let { "200" }

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
