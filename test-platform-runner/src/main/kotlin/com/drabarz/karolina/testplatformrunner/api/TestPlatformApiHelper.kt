package com.drabarz.karolina.testplatformrunner.api

import com.drabarz.karolina.testplatformrunner.model.UsersAuthRepository
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.*

fun createFileResponse(file: File): ResponseEntity<*> {
    val headers = HttpHeaders()
    headers.add("X-Suggested-Filename", file.name);
    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.name + "\"")
    headers.add("Access-Control-Expose-Headers", HttpHeaders.CONTENT_DISPOSITION + "," + HttpHeaders.CONTENT_LENGTH)

    return ResponseEntity.ok().headers(headers).body<Any>(file.readBytes())
}

@Component
class TestPlatformApiHelper(val usersAuthRepository: UsersAuthRepository) {

    fun getUserNameFromRequestHeader(headers: HttpHeaders): String {
        val authorization = headers.get("Authorization")?.get(0)
        println("BASIC_AUTH: " + authorization)
        if (authorization != null && authorization!!.toLowerCase().startsWith("basic")) {
            // Authorization: Basic base64credentials
            val base64Credentials = authorization!!.substring("Basic".length).trim({ it <= ' ' })
            val credDecoded = Base64.getDecoder().decode(base64Credentials)
            val credentials = String(credDecoded, StandardCharsets.UTF_8)
            // credentials = username:password
            val values = credentials.split(":".toRegex(), 2).toTypedArray()
            println("USER_NAME: " + values[0])

            return values[0]
        }

        throw IllegalAccessError()
    }
}