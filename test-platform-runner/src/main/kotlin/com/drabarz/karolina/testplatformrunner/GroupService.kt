package com.drabarz.karolina.testplatformrunner

import com.drabarz.karolina.testplatformrunner.model.*
import com.drabarz.karolina.testplatformrunner.model.Group
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import javax.transaction.Transactional

@Component
class GroupService(
        val usersRepository: UsersRepository,
        val groupsRepository: GroupsRepository,
        val projectsRepository: ProjectsRepository) {

    @Transactional
    fun addStudent(studentName: String): String {
        log.info("Adding student: $studentName")
        usersRepository.save(User(name = studentName, isStudent = true))

        return "200"
    }

    fun getStudents(): List<String> {
        return usersRepository.findAllByIsStudentIsTrue().map { it.name }
    }

    @Transactional
    fun addGroup(groupName: String, projectName: String): String {
        groupsRepository.save(Group(name = groupName, project = projectsRepository.findByName(projectName)))
        return "200"
    }

    fun getGroups(projectName: String): GroupsResponse {
        val groups = groupsRepository.findAllByProject_Name(projectName)

        return GroupsResponse(groups
                .map {
                    com.drabarz.karolina.testplatformrunner.Group(
                            it.name,
                            it.project.name,
                            it.students.map { it.name })
                }
        )
    }

    @Transactional
    fun addGroups(groups: GroupsDao, projectName: String): String {
        groups.groups.forEach { addGroupWithStudents(it, projectName) }

        return "200"
    }

    private fun addGroupWithStudents(groupDao: GroupDao, projectName: String) {
        log.info("Adding group: ${groupDao.name}")
        val group = groupsRepository.save(
                Group(
                        name = groupDao.name,
                        project = projectsRepository.findByName(projectName)))

        groupDao.students.forEach { addStudent(it) }

        log.info("Adding students to group")
        groupDao.students.forEach { studentName ->
            usersRepository.findByName(studentName).groups.add(group)
                    .also { log.info("Student: $studentName added to group ${group.name}") } }
    }

    @Transactional
    fun addStudentToGroup(projectName:String, groupName:String, studentName: String): String {
        addStudent(studentName)

        log.info("Adding student to group")
        val group = groupsRepository.findByName(groupName)

        usersRepository.findByName(studentName).groups.add(group)
                    .also { log.info("Student: $studentName added to group ${group.name}") }

        return "200"
    }

    @Transactional
    fun removeStudentFromGroup(projectName: String, groupName: String, studentName: String): String {
        //TODO: Should be rather projectName and groupName
        val student = usersRepository.findByName(studentName)
        student.groups.remove(groupsRepository.findByName(groupName))

        usersRepository.save(student)

        return "200"
    }

    fun deleteGroup(groupName: String, projectName: String): String {
        val group = groupsRepository.findByName(groupName)

        group.students.forEach { removeStudentFromGroup(projectName, groupName, it.name) }

        groupsRepository.delete(group)

        return "200"
    }

    companion object {
        val log = LoggerFactory.getLogger(GroupService::class.java)
    }
}