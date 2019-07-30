package com.drabarz.karolina.testplatformrunner

import com.drabarz.karolina.testplatformrunner.model.*
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.lang.RuntimeException

@Component
class IntegrationService(
        val integrationsRepository: IntegrationsRepository,
        val integrationStagesRepository: IntegrationStagesRepository,
        val stagesRepository: StagesRepository,
        val projectsRepository: ProjectsRepository,
        val pathProvider: IntegrationPathProvider) {

    private final val testCaseService = TestCaseService(pathProvider)

    fun addIntegration(projectName: String, integrationName: String, integrationStages: List<IntegrationStageDao>): String {
        val projectDir = pathProvider.getProjectDir(projectName)

        if (!projectDir.exists()) {
            throw RuntimeException("Error. Can not create test case for project. Project $projectName doesn't exist")
        }

        val integrationDir = pathProvider.getTaskDir(projectName, integrationName)

        integrationDir.mkdirs()

        val integration = integrationsRepository.save(
                Integration(
                        name = integrationName,
                        project = projectsRepository.findByName(projectName)
                )
        )

        integrationStages.forEach {
            if (stagesRepository.findByNameAndProject_Name(it.stageName, projectName) != null) {
                integrationStagesRepository.save(IntegrationStage(
                        name = it.name,
                        orderNumber = it.orderNumber,
                        stage = stagesRepository.findByNameAndProject_Name(it.stageName, projectName)!!,
                        integration = integration
                ))
            }
        }

        return "200"
    }

    fun getIntegrations(projectName: String): IntegrationsDao {
        return IntegrationsDao(integrationsRepository.findAllByProject_Name(projectName)
                .map {
                    IntegrationDao(
                            it.name,
                            getIntegrationStages(it),
                            testCaseService.getTestCases(projectName, it.name))
                })
    }

    private fun getIntegrationStages(it: Integration): List<IntegrationStageDao> {
        return integrationStagesRepository.findAllByIntegration(it)
                .map { IntegrationStageDao(it.name, it.orderNumber, it.stage.name) }
                .sortedBy { it.orderNumber }
    }

    fun editTestCaseParameters(projectName: String, integrationName: String, testCaseName: String, parameters: String?): String {
        return testCaseService.editParameters(projectName, integrationName, testCaseName, parameters)
    }

    fun saveTestCase(inputFile: MultipartFile, outputFile: MultipartFile, projectName: String, integrationName: String, testCaseName: String): String {
        return testCaseService.saveTestCase(inputFile, outputFile, projectName, integrationName, testCaseName)
    }

    fun getTestCaseFile(projectName: String, integrationName: String, testCaseName: String, fileType: String): File {
        return testCaseService.getTestCaseFile(projectName, integrationName, testCaseName, fileType)
    }

    fun uploadTestCaseFile(projectName: String, integrationName: String, testCaseName: String, fileType: String, file: MultipartFile): String {
        return testCaseService.uploadTestCaseFile(projectName, integrationName, testCaseName, fileType, file)
    }

    fun deleteTestCase(projectName: String, integrationName: String, testCaseName: String): String {
        return testCaseService.deleteTestCase(projectName, integrationName, testCaseName)
    }
}