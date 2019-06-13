package com.drabarz.karolina.testplatformrunner

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
            ProjectService.log.info("Existing file to delete: " + dir.list().first() + " from: " + dir.absolutePath)
            dir.listFiles().first().delete()
        }
    }
}