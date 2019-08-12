package com.drabarz.karolina.testplatformrunner.api

import com.drabarz.karolina.testplatformrunner.model.User
import com.drabarz.karolina.testplatformrunner.model.UsersAuthRepository
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import java.io.File

fun createFileResponse(file: File): ResponseEntity<*> {
    val headers = HttpHeaders()
    headers.add("X-Suggested-Filename", file.name)
    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.name + "\"")
    headers.add("Access-Control-Expose-Headers", HttpHeaders.CONTENT_DISPOSITION + "," + HttpHeaders.CONTENT_LENGTH)

    return ResponseEntity.ok().headers(headers).body<Any>(file.readBytes())
}

@Component
class TestPlatformApiHelper(val usersAuthRepository: UsersAuthRepository) {

    fun getUserNameFromRequestHeader(headers: HttpHeaders): String =
            getUser(headers).name

    fun isLecturerOrThrow(headers: HttpHeaders): User {
        val user = getUser(headers)
        if (!user.isStudent) {
            return user
        }
        throw IllegalAccessError()
    }

    fun getUser(headers: HttpHeaders): User {
        return headers["Authorization"]
                ?.get(0)
                ?.removePrefix("token ")
                ?.let { usersAuthRepository.findByToken(it) }
                ?.user
                ?: throw IllegalAccessError()
    }
}