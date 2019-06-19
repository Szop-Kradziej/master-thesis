package com.drabarz.karolina.testplatformrunner.model

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.ArrayList
import javax.persistence.*

@Entity
@Table(name = "groups")
data class Group(
        @Id
        @SequenceGenerator(name = "groups_id_generator", sequenceName = "groups_id_seq", allocationSize = 1)
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "groups_id_generator")
        val id: Long = -1,
        val name: String = "",
        @ManyToOne
        @JoinColumn(name = "project_id")
        val project: Project,
        @ManyToMany(mappedBy = "groups")
        val students: MutableList<User> = ArrayList()
)

@Repository
interface GroupsRepository : CrudRepository<Group, Long> {
    fun findByName(name: String): Group
    fun findAllByProject_Name(projectName: String): List<Group>
}