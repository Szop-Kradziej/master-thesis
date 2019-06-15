package com.drabarz.karolina.testplatformrunner

import com.drabarz.karolina.testplatformrunner.model.User
import com.drabarz.karolina.testplatformrunner.model.UsersRepository
import org.springframework.stereotype.Component

@Component
class GroupService(val usersRepository: UsersRepository) {

    fun addStudent(studentName: String): String {
        usersRepository.save(User(name = studentName, isStudent = true))

        return "200"
    }

    fun getStudents(): List<String> {
        return usersRepository.findAllByIsStudentIsTrue().map { it.name }
    }
}