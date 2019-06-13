package com.drabarz.karolina.testplatformrunner

import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File

@Component
class StageService(
        val pathProvider: PathProvider,
        val testCaseService: TestCaseService,
        val deleteFileService: DeleteFileService) {

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

    fun getStages(projectName: String): List<Stage> {
        val projectDir = pathProvider.getProjectDir(projectName)
        if (!projectDir.exists()) {
            throw RuntimeException("Error. Can not get stages for project. Project $projectName doesn't exist")
        }
        val stagesDir = pathProvider.getStagesDir(projectName)
        if (!stagesDir.exists()) {
            return emptyList()
        }

        return stagesDir.list().asList()
                .map {
                    Stage(
                            it,
                            getStageDescriptionName(projectName, it),
                            testCaseService.getTestCasesNames(projectName, it))
                }
    }

    fun addStageDescription(uploadedFile: MultipartFile, projectName: String, stageName: String): String {
        val stageDir = pathProvider.getStageDir(projectName, stageName)
        if (!stageDir.exists()) {
            throw RuntimeException("Error. Stage $stageName for project: $projectName doesn't exist")
        }

        val descriptionDir = pathProvider.getStageDescriptionDir(projectName, stageName)
        descriptionDir.mkdir()

        deleteFileService.deleteSingleFileFromDir(descriptionDir)

        val outputFile = File(descriptionDir.path, uploadedFile.originalFilename)
        uploadedFile.transferTo(outputFile)

        return "200"
    }

    fun getStageDescriptionName(projectName: String, stageName: String): String? {
        val stageDescriptionDir = pathProvider.getStageDescriptionDir(projectName, stageName)
        if (!stageDescriptionDir.exists() || stageDescriptionDir.list().size != 1) {
            return null
        }
        return stageDescriptionDir.list()[0]
    }


    fun getStageDescription(projectName: String, stageName: String): File {
        val stageDescriptionDir = pathProvider.getStageDescriptionDir(projectName, stageName)
        if (stageDescriptionDir.exists() && stageDescriptionDir.list().size == 1) {
            return File(stageDescriptionDir, stageDescriptionDir.list()[0])
        }

        throw java.lang.RuntimeException("Error file doesn't exist")
    }

    fun deleteStage(projectName: String, stageName: String): String {
        val stageDir = pathProvider.getStageDir(projectName, stageName)

        if (stageDir.exists()) {
            if (stageDir.list().isNotEmpty()) {
                deleteTestCases(projectName, stageName)
                deleteStageDescriptionDir(projectName, stageName)
            }
            stageDir.delete()
        }

        return "200"
    }

    private fun deleteTestCases(projectName: String, stageName: String) {
        val testCasesDir = pathProvider.getTestCasesDir(projectName, stageName)
        if (!testCasesDir.exists()) {
            return
        }

        testCasesDir.list()
                .forEach { testCaseService.deleteTestCase(projectName, stageName, it) }

        testCasesDir.delete()
    }

    private fun deleteStageDescriptionDir(projectName: String, stageName: String) {
        val stageDir = pathProvider.getStageDir(projectName, stageName)

        if (stageDir.list().contains(PathProvider.DESCRIPTION)) {
            val descriptionDir = pathProvider.getStageDescriptionDir(projectName, stageName)
            deleteFileService.deleteSingleFileWithDirectory(descriptionDir)
        }
    }
}