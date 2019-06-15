package com.drabarz.karolina.testplatformrunner.model

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "projects")
data class Project(
        @Id @GeneratedValue
        val id: Long = -1,
        val name: String = ""
)

@Repository
interface ProjectsRepository : CrudRepository<Project, Long> {
    override fun findAll(): MutableList<Project>
}