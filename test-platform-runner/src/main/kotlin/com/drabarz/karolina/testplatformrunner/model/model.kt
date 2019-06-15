package com.drabarz.karolina.testplatformrunner.model

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.persistence.*

@Entity
@Table(name = "projects")
data class Project(
        @Id
        @SequenceGenerator(name = "projects_id_generator", sequenceName = "projects_id_seq", allocationSize = 1)
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "projects_id_generator")
        val id: Long = -1,
        val name: String = ""
)

@Repository
interface ProjectsRepository : CrudRepository<Project, Long> {
    override fun findAll(): MutableList<Project>
    fun deleteByName(name: String)
}

@Entity
@Table(name = "users")
data class User(
        @Id
        @SequenceGenerator(name = "users_id_generator", sequenceName = "users_id_seq", allocationSize = 1)
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_generator")
        val id: Long = -1,
        val name: String = "",
        val password: String = "password",
        val isStudent: Boolean = true
)

@Repository
interface UsersRepository : CrudRepository<User, Long>
