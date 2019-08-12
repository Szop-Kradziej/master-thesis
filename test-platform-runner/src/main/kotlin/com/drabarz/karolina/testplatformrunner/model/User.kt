package com.drabarz.karolina.testplatformrunner.model

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.persistence.*

@Entity
@Table(name = "users")
data class User(
        @Id
        @SequenceGenerator(name = "users_id_generator", sequenceName = "users_id_seq", allocationSize = 1)
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_generator")
        val id: Long = -1,
        val name: String = "",
        val isStudent: Boolean = true,
        @ManyToMany(cascade = arrayOf(CascadeType.ALL))
        @JoinTable(
                name = "students_in_groups",
                joinColumns = arrayOf(JoinColumn(name = "student_id")),
                inverseJoinColumns = arrayOf(JoinColumn(name = "group_id"))
        )
        val groups: MutableList<Group> = ArrayList()
)

@Repository
interface UsersRepository : CrudRepository<User, Long> {
    fun findAllByIsStudentIsTrue(): MutableList<User>
    fun findByName(name: String): User?
}

@Entity
@Table(name = "users_auth")
data class UserAuth(
        @Id
        @SequenceGenerator(name = "users_auth_id_generator", sequenceName = "users_auth_id_seq", allocationSize = 1)
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_auth_id_generator")
        val id: Long = -1,
        val token: String,
        @ManyToOne
        @JoinColumn(name = "user_id")
        val user: User
)

@Repository
interface UsersAuthRepository : CrudRepository<UserAuth, Long> {
    fun findByToken(token: String): UserAuth?
}