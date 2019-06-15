package com.drabarz.karolina.testplatformrunner

import com.drabarz.karolina.testplatformrunner.model.*
import org.springframework.stereotype.Component

@Component
class GroupService(
        val usersRepository: UsersRepository,
        val groupsRepository: GroupsRepository,
        val projectsRepository: ProjectsRepository) {

    fun addStudent(studentName: String): String {
        usersRepository.save(User(name = studentName, isStudent = true))

        return "200"
    }

    fun getStudents(): List<String> {
        return usersRepository.findAllByIsStudentIsTrue().map { it.name }
    }

    fun addGroup(groupName:String, projectName:String):String {
        groupsRepository.save(Group(name = groupName, project = projectsRepository.findByName(projectName)))
        return "200"
    }
}