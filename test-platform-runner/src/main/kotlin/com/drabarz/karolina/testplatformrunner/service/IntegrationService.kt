package com.drabarz.karolina.testplatformrunner.service

import com.drabarz.karolina.testplatformrunner.api.IntegrationDao
import com.drabarz.karolina.testplatformrunner.api.IntegrationStageDao
import com.drabarz.karolina.testplatformrunner.api.IntegrationsDao
import com.drabarz.karolina.testplatformrunner.model.*
import com.drabarz.karolina.testplatformrunner.service.helper.IntegrationPathProvider
import org.slf4j.LoggerFactory
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

    fun getIntegrations(projectName: String): IntegrationsDao {
        log.info("Getting all integrations for project: $projectName")

        return IntegrationsDao(integrationsRepository.findAllByProject_Name(projectName)
                .map {
                    val integrationMetadata: Integration =
                            integrationsRepository.findByNameAndProject_Name(it.name, projectName)
                    IntegrationDao(
                            it.name,
                            getIntegrationStages(it),
                            integrationMetadata.comment,
                            testCaseService.getIntegrationTestCases(projectName, it.name, getIntegrationStages(it).size))
                })
    }

    fun addIntegration(projectName: String, integrationName: String, integrationStages: List<IntegrationStageDao>): String {
        log.info("Adding integration: $integrationName for project: $projectName")

        val projectDir = pathProvider.getProjectDir(projectName)

        if (!projectDir.exists()) {
            log.error("Can not create integration: $integrationName for project $projectName, project doesn't exist")
            throw RuntimeException("Error. Can not create integration for project. Project $projectName doesn't exist")
        }

        val integrationDir = pathProvider.getTaskDir(projectName, integrationName)

        if (integrationDir.exists()) {
            log.warn("Can not create integration: $integrationName for project $projectName, stage already exist")
            throw RuntimeException("Warning. Integration already exists")
        }

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

        log.info("Integration: $integrationName for project $projectName created")

        return SUCCESS_RESPONSE
    }

    fun editComment(projectName: String, integrationName: String, comment: String?): String {
        log.info("Editing comment for integration: $integrationName in project: $projectName")

        integrationsRepository.findByNameAndProject_Name(integrationName, projectName)
                .also { it.comment = comment }
                .let { integrationsRepository.save(it) }

        log.info("Comment for integration $integrationName in project $projectName edited")

        return SUCCESS_RESPONSE
    }

    fun deleteIntegration(projectName: String, integrationName: String): String {
        log.info("Deleting integration: $integrationName for project: $projectName")

        val integrationDir = pathProvider.getTaskDir(projectName, integrationName)

        if (integrationDir.exists()) {
            if (integrationDir.list().isNotEmpty()) {
                testCaseService.deleteTestCases(projectName, integrationName)
            }
            integrationDir.delete()
        }

        val integration = integrationsRepository.findByNameAndProject_Name(integrationName, projectName)

        integrationStagesRepository.findAllByIntegration(integration)
                .forEach { it -> integrationStagesRepository.delete(it) }

        integrationsRepository.delete(integration)

        log.info("Integration: $integrationName for project: $projectName deleted")

        return SUCCESS_RESPONSE
    }

    fun getIntegrationStages(projectName: String, integrationName: String): List<IntegrationStageDao> {
        log.info("Getting assigned stages for integration: $integrationName in project: $projectName")

        val integration = integrationsRepository.findByNameAndProject_Name(integrationName, projectName)

        return getIntegrationStages(integration)
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
        return testCaseService.addTestCase(inputFile, outputFile, projectName, integrationName, testCaseName)
    }

    fun getTestCaseFile(projectName: String, integrationName: String, testCaseName: String, fileType: String): File {
        return testCaseService.getTestCaseFile(projectName, integrationName, testCaseName, fileType)
    }

    fun getParametersTestCaseFile(projectName: String, integrationName: String, testCaseName: String, index: Int): File {
        return testCaseService.getIntegrationParametersTestCaseFile(projectName, integrationName, testCaseName, index)
    }

    fun uploadTestCaseFile(projectName: String, integrationName: String, testCaseName: String, fileType: String, file: MultipartFile): String {
        return testCaseService.uploadTestCaseFile(projectName, integrationName, testCaseName, fileType, file)
    }

    fun deleteTestCase(projectName: String, integrationName: String, testCaseName: String): String {
        return testCaseService.deleteTestCase(projectName, integrationName, testCaseName)
    }

    fun uploadParametersTestCaseFile(projectName: String, integrationName: String, testCaseName: String, index: Int, file: MultipartFile): String {
        return testCaseService.uploadIntegrationParametersTestCaseFile(projectName, integrationName, testCaseName, index, file)
    }


    companion object {
        val log = LoggerFactory.getLogger(IntegrationService::class.java)
        const val SUCCESS_RESPONSE = "200"
    }
}