package com.drabarz.karolina.testplatformrunner

import com.drabarz.karolina.testplatformrunner.model.*
import org.springframework.stereotype.Component
import java.lang.RuntimeException

@Component
class IntegrationService(
        val integrationsRepository: IntegrationsRepository,
        val integrationStagesRepository: IntegrationStagesRepository,
        val stagesRepository: StagesRepository,
        val projectsRepository: ProjectsRepository,
        val pathProvider: PathProvider) {

    fun addIntegration(projectName: String, integrationName: String, integrationStages: List<IntegrationStageDao>): String {
        val projectDir = pathProvider.getProjectDir(projectName)

        if(!projectDir.exists()) {
            throw RuntimeException("Error. Can not create test case for project. Project $projectName doesn't exist")
        }

        val integrationDir = pathProvider.getIntegrationDir(projectName, integrationName)

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

    //TODO: Fix empty list
    fun getIntegrations(projectName: String): IntegrationsDao {
        return IntegrationsDao(integrationsRepository.findAllByProject_Name(projectName)
                .map { IntegrationDao(it.name, emptyList()) })
    }
}