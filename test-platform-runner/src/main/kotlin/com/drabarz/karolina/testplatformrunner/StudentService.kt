package com.drabarz.karolina.testplatformrunner

import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File

@Component
class StudentService(val testCaseService: TestCaseService, val pathProvider: PathProvider) {

    fun saveFile(projectName:String, stageName: String, uploadedFile: MultipartFile, fileType: FileType) {
        val pathPrefix = "${pathProvider.jarPath}/$projectName/$stageName"
        var dir = File(pathPrefix)
        if (fileType == FileType.BINARY) {
            dir = File("$pathPrefix/bin")
        }
        else {
            dir = File("$pathPrefix/report")
        }

        dir.mkdirs()

        //TODO: Delete this file
        if(dir.list().isNotEmpty()) {
            JarService.log.info("Existing file to delete: " + File("${dir.path}/bin/${dir.list()[0]}").path)
        }

        val outputFile = File(dir.path, uploadedFile.originalFilename)
        uploadedFile.transferTo(outputFile)
    }

    fun getStudentStages(projectName: String): List<StudentStage> {
        return testCaseService
                .getStages(projectName)
                .map { stage -> StudentStage(stage.stageName, getBinary(projectName, stage.stageName), getReport(projectName, stage.stageName), stage.testCases)}
    }

    private fun getBinary(projectName: String, stageName: String): String? {
        return getStageFile(projectName, stageName, FileType.BINARY)
    }

    private fun getReport(projectName: String, stageName: String): String? {
        return getStageFile(projectName, stageName, FileType.REPORT)
    }

    private fun getStageFile(projectName: String, stageName: String, fileType: FileType): String? {

        val stageDir = File("${pathProvider.jarPath}/$projectName/$stageName")

        if (!stageDir.exists()) {
            return null
        }

        val fileDir: File
        if (fileType == FileType.BINARY) {
            fileDir = File("$stageDir/bin")
        }
        else {
            fileDir = File("$stageDir/report")
        }

        if (!fileDir.exists() || fileDir.list().size != 1) {
            return null
        }

        return fileDir.list()[0]
    }
}

enum class FileType {
    BINARY,
    REPORT
}