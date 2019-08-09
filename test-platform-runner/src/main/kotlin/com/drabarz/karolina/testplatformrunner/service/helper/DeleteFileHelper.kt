package com.drabarz.karolina.testplatformrunner.service.helper

import com.drabarz.karolina.testplatformrunner.service.TestCaseService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.File

@Component
class DeleteFileHelper {

    fun deleteSingleFileWithDirectory(dir: File) {
        log.info("Deleting directory ${dir.absolutePath}")

        deleteSingleFileFromDir(dir)
        dir.delete()

        log.info("Directory deleted")
    }

    fun deleteSingleFileFromDir(dir: File) {
        log.info("Deleting single file from directory: ${dir.absolutePath}")

        if (dir.list().isNotEmpty()) {
            log.info("Existing file to delete: ${dir.list().first()} from: ${dir.absolutePath}")

            dir.listFiles().first().delete()

            log.info("File deleted")
        }
    }

    companion object {
        val log = LoggerFactory.getLogger(DeleteFileHelper::class.java)
    }
}