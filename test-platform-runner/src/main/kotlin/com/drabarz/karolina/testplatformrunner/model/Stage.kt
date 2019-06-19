package com.drabarz.karolina.testplatformrunner.model

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "stages")
data class Stage(
        @Id
        @SequenceGenerator(name = "stages_id_generator", sequenceName = "stages_id_seq", allocationSize = 1)
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stages_id_generator")
        val id: Long = -1,
        val name: String = "",
        @ManyToOne
        @JoinColumn(name = "project_id")
        val project: Project,
        val startDate: Date? = Date(),
        val endDate: Date? = Date(),
        val pointsNumber: Int? = 0
)

@Repository
interface StagesRepository : CrudRepository<Stage, Long> {
    fun findByName(name: String): Stage?
    fun findAllByProject_Name(projectName: String): List<Stage>
}