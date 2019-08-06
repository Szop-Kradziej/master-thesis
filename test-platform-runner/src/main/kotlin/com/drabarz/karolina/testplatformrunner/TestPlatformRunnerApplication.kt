package com.drabarz.karolina.testplatformrunner

import com.drabarz.karolina.testplatformrunner.service.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.GetMapping
import java.io.File
import java.util.*

@SpringBootApplication
class TestPlatformRunnerApplication

fun main(args: Array<String>) {
    TestPlatformRunnerApplication::class.java.getResource("/static/Dockerfile").readText().let { print(it) }
    runApplication<TestPlatformRunnerApplication>(*args)
}
