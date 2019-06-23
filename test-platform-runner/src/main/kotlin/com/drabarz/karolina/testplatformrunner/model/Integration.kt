package com.drabarz.karolina.testplatformrunner.model

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.persistence.*

@Entity
@Table(name = "integrations")
data class Integration(
        @Id
        @SequenceGenerator(name = "integrations_id_generator", sequenceName = "integrations_id_seq", allocationSize = 1)
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "integrations_id_generator")
        val id: Long = -1,
        val name: String = "",
        @ManyToOne
        @JoinColumn(name = "project_id")
        val project: Project
)

@Repository
interface IntegrationsRepository : CrudRepository<Integration, Long> {
    fun findAllByProject_Name(projectName: String): List<Integration>
}