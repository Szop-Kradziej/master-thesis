package com.drabarz.karolina.testplatformrunner.api

import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import java.io.File

fun createFileResponse(file: File): ResponseEntity<*> {
    val headers = HttpHeaders()
    headers.add("X-Suggested-Filename", file.name);
    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.name + "\"")
    headers.add("Access-Control-Expose-Headers", HttpHeaders.CONTENT_DISPOSITION + "," + HttpHeaders.CONTENT_LENGTH)

    return ResponseEntity.ok().headers(headers).body<Any>(file.readBytes())
}