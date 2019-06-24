package com.drabarz.karolina.testplatformrunner

import com.drabarz.karolina.testplatformrunner.model.Integration
import com.drabarz.karolina.testplatformrunner.model.IntegrationsRepository
import com.drabarz.karolina.testplatformrunner.model.ProjectsRepository
import org.springframework.stereotype.Component
import java.lang.RuntimeException

@Component
class IntegrationService(
        val integrationsRepository: IntegrationsRepository,
        val projectsRepository: ProjectsRepository,
        val pathProvider: PathProvider) {

    fun addIntegration(projectName: String, integrationName: String): String {
        val projectDir = pathProvider.getProjectDir(projectName)

        if(!projectDir.exists()) {
            throw RuntimeException("Error. Can not create test case for project. Project $projectName doesn't exist")
        }

        val integrationDir = pathProvider.getIntegrationDir(projectName, integrationName)

        integrationDir.mkdirs()

        integrationsRepository.save(
                Integration(
                        name = integrationName,
                        project = projectsRepository.findByName(projectName)
                )
        )

        return "200"
    }

    fun getIntegrations(projectName: String): IntegrationsDao {
        return IntegrationsDao(integrationsRepository.findAllByProject_Name(projectName)
                .map { IntegrationDao(it.name) })
    }
}