package com.drabarz.karolina.testplatformrunner.service

import com.drabarz.karolina.testplatformrunner.api.StudentGroup
import com.drabarz.karolina.testplatformrunner.api.StudentIntegration
import com.drabarz.karolina.testplatformrunner.api.StudentStage
import com.drabarz.karolina.testplatformrunner.model.GroupsRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.lang.RuntimeException

@Component
class StudentService(
        val groupResultService: GroupResultService,
        val groupsRepository: GroupsRepository) {

    fun getStudentProjects(studentName: String?): List<String> {
        log.info("Getting all projects for student: $studentName")

        if (studentName == null) {
            log.error("Invalid student name: $studentName")
            throw RuntimeException("Invalid student name: $studentName")
        }

        return groupsRepository.findAllByStudents_Name(studentName).map { it -> it.project.name }
    }

    fun runStageTests(studentName: String?, projectName: String, stageName: String): List<TestResponse> {
        val groupName = getGroupName(studentName, projectName)
        return groupResultService.runStageTests(studentName!!, groupName, projectName, stageName)
    }

    fun runIntegrationTests(studentName: String?, projectName: String, integrationName: String): List<TestResponse> {
        val groupName = getGroupName(studentName, projectName)
        return groupResultService.runIntegrationTests(studentName!!, groupName, projectName, integrationName)
    }


    fun saveFile(studentName: String?, projectName: String, stageName: String, uploadedFile: MultipartFile, fileType: FileType) {
        val groupName = getGroupName(studentName, projectName)
        groupResultService.saveFile(studentName!!, groupName, projectName, stageName, uploadedFile, fileType)
    }

    fun getStudentStages(studentName: String?, projectName: String): List<StudentStage> {
        val groupName = getGroupName(studentName, projectName)
        return groupResultService.getStudentStages(groupName, projectName)
    }

    fun saveCodeLink(studentName: String?, projectName: String, stageName: String, codeLink: String): String {
        val groupName = getGroupName(studentName, projectName)
        return groupResultService.saveCodeLink(studentName!!, groupName, projectName, stageName, codeLink)
    }

    fun getBin(studentName: String?, projectName: String, stageName: String): File {
        val groupName = getGroupName(studentName, projectName)
        return groupResultService.getBin(groupName, projectName, stageName)
    }

    fun getReport(studentName: String?, projectName: String, stageName: String): File {
        val groupName = getGroupName(studentName, projectName)
        return groupResultService.getReport(groupName, projectName, stageName)
    }

    fun getStageLogsFile(studentName: String?, projectName: String, stageName: String, testCaseName: String): File {
        val groupName = getGroupName(studentName, projectName)
        return groupResultService.getStageLogsFile(groupName, projectName, stageName, testCaseName)
    }

    fun getStudentIntegrations(studentName: String?, projectName: String): List<StudentIntegration> {
        val groupName = getGroupName(studentName, projectName)
        return groupResultService.getStudentIntegrations(groupName, projectName)
    }

    fun getIntegrationLogsFile(studentName: String?, projectName: String, integrationName: String, testCaseName: String): File {
        val groupName = getGroupName(studentName, projectName)
        return groupResultService.getIntegrationLogsFile(groupName, projectName, integrationName, testCaseName)
    }

    private fun getGroupName(studentName: String?, projectName: String): String {
        log.info("Getting group name for student: $studentName in project: $projectName")

        if (studentName == null) {
            log.error("Invalid student name: $studentName")
            throw RuntimeException("Invalid student name: $studentName")
        }

        val group = groupsRepository.findAllByStudents_Name(studentName)
                .filter { it.project.name == projectName }

        log.info("Found ${group.size} groups for student: $studentName")

        return group.first().name
    }

    fun getStudentGroup(studentName: String, projectName: String): StudentGroup {
        log.info("Getting group for student: $studentName in project: $projectName")

        val groupName = getGroupName(studentName, projectName)
        val students = groupsRepository.findByNameAndProject_Name(groupName, projectName).students.map { it.name }

        return StudentGroup(groupName, students)
    }

    companion object {
        val log = LoggerFactory.getLogger(StudentService::class.java)
    }
}