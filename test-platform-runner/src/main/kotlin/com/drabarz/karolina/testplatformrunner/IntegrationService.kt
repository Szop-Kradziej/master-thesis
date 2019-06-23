package com.drabarz.karolina.testplatformrunner

import com.drabarz.karolina.testplatformrunner.model.Integration
import com.drabarz.karolina.testplatformrunner.model.IntegrationsRepository
import com.drabarz.karolina.testplatformrunner.model.ProjectsRepository
import org.springframework.stereotype.Component

@Component
class IntegrationService(
        val integrationsRepository: IntegrationsRepository,
        val projectsRepository: ProjectsRepository) {
    fun addIntegration(projectName: String, integrationName: String): String {
        integrationsRepository.save(
                Integration(
                        name = integrationName,
                        project = projectsRepository.findByName(projectName)
                )
        )

        return "200"
    }
}