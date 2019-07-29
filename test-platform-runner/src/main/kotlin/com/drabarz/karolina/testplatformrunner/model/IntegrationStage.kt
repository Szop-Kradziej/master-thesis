package com.drabarz.karolina.testplatformrunner.model

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.persistence.*

@Entity
@Table(name = "stages_in_integrations")
data class IntegrationStage(
        @Id
        @SequenceGenerator(name = "stages_in_integrations_id_generator", sequenceName = "stages_in_integrations_id_seq", allocationSize = 1)
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stages_in_integrations_id_generator")
        val id: Long = -1,
        val name: String = "",
        val orderNumber: Int = 0,
        @ManyToOne
        @JoinColumn(name = "integration_id")
        val integration: Integration,
        @ManyToOne
        @JoinColumn(name = "stage_id")
        val stage: Stage
)

@Repository
interface IntegrationStagesRepository : CrudRepository<IntegrationStage, Long> {
    fun findAllByIntegration(integration: Integration): List<IntegrationStage>
}