package com.drabarz.karolina.testplatformrunner.service.helper

import com.drabarz.karolina.testplatformrunner.service.TestCaseService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.File

@Component
class DeleteFileHelper {

    fun deleteSingleFileWithDirectory(dir: File) {
        deleteSingleFileFromDir(dir)
        dir.delete()
    }

    fun deleteSingleFileFromDir(dir: File) {
        if (dir.list().isNotEmpty()) {
            log.info("Existing file to delete: " + dir.list().first() + " from: " + dir.absolutePath)
            dir.listFiles().first().delete()
        }
    }

    companion object {
        val log = LoggerFactory.getLogger(TestCaseService::class.java)
    }
}