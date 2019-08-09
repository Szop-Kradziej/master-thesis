package com.drabarz.karolina.testplatformrunner.service

import com.drabarz.karolina.testplatformrunner.api.GroupDao
import com.drabarz.karolina.testplatformrunner.api.GroupsDao
import com.drabarz.karolina.testplatformrunner.api.GroupsResponse
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

    fun getGroups(projectName: String): GroupsResponse {
        val groups = groupsRepository.findAllByProject_Name(projectName)

        return GroupsResponse(groups
                .map {
                    com.drabarz.karolina.testplatformrunner.api.Group(
                            it.name,
                            it.project.name,
                            it.students.map { it.name })
                }
        )
    }

    fun getStudentsAssignedToGroup(projectName: String, groupName: String): List<String> {
        log.info("Gettind all students assigned to group: $groupName in project $projectName")

        return groupsRepository.findByNameAndProject_Name(groupName, projectName).students.map { it.name }
    }

    @Transactional
    fun addGroup(groupName: String, projectName: String): String {
        log.info("Adding group: $groupName for project: $projectName")

        groupsRepository.save(Group(name = groupName, project = projectsRepository.findByName(projectName)))

        log.info("Group: $groupName for project: $projectName created")

        return SUCCESS_RESPONSE
    }

    @Transactional
    fun addGroups(groups: GroupsDao, projectName: String): String {
        log.info("Adding ${groups.groups.size} groups for project: $projectName")

        groups.groups.forEach { addGroupWithStudents(it, projectName) }

        log.info("All groups created")

        return SUCCESS_RESPONSE
    }

    private fun addGroupWithStudents(groupDao: GroupDao, projectName: String) {
        val group = groupsRepository.save(
                Group(
                        name = groupDao.name,
                        project = projectsRepository.findByName(projectName)))

        groupDao.students.forEach { addStudent(it) }

        log.info("Adding ${groupDao.students.size} students to group: ${groupDao.name}")
        groupDao.students.forEach { studentName ->
            usersRepository.findByName(studentName).groups.add(group)
                    .also { log.info("Student: $studentName added to group: ${group.name}") }
        }

        log.info("All students added to group: ${groupDao.name}")
    }

    @Transactional
    fun addStudentToGroup(projectName:String, groupName:String, studentName: String): String {
        log.info("Adding student: $studentName to group: $groupName in project: $projectName")

        addStudent(studentName)

        log.info("Adding student to group: $groupName")
        val group = groupsRepository.findByName(groupName)

        usersRepository.findByName(studentName).groups.add(group)
                    .also { log.info("Student: $studentName added to group ${group.name}") }

        return SUCCESS_RESPONSE
    }

    @Transactional
    fun addStudent(studentName: String): String {
        log.info("Adding student: $studentName")

        usersRepository.save(User(name = studentName, isStudent = true))

        log.info("Student: $studentName added")

        return SUCCESS_RESPONSE
    }

    @Transactional
    fun removeStudentFromGroup(projectName: String, groupName: String, studentName: String): String {
        log.info("Deleting student: $studentName from group: $groupName in project $projectName")

        val student = usersRepository.findByName(studentName)
        student.groups.remove(groupsRepository.findByName(groupName))

        usersRepository.save(student)

        log.info("Student $studentName deleted from group $groupName")

        return SUCCESS_RESPONSE
    }

    fun deleteGroup(groupName: String, projectName: String): String {
        log.info("Deleting group: $groupName from project $projectName")

        val group = groupsRepository.findByName(groupName)

        group.students.forEach { removeStudentFromGroup(projectName, groupName, it.name) }

        groupsRepository.delete(group)

        log.info("Group: $groupName in project: $projectName deleted")

        return SUCCESS_RESPONSE
    }

    companion object {
        val log = LoggerFactory.getLogger(GroupService::class.java)
        const val SUCCESS_RESPONSE = "200"
    }
}