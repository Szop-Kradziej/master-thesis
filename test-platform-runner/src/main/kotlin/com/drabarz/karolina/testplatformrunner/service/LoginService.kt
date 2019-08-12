package com.drabarz.karolina.testplatformrunner.service

import com.drabarz.karolina.testplatformrunner.api.*
import com.drabarz.karolina.testplatformrunner.model.UserAuth
import com.drabarz.karolina.testplatformrunner.model.UsersAuthRepository
import com.drabarz.karolina.testplatformrunner.model.UsersRepository
import org.slf4j.LoggerFactory
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import java.lang.RuntimeException

@Component
class LoginService(val usersRepository: UsersRepository,
                   val usersAuthRepository: UsersAuthRepository) {

    fun loginUser(loginRequest: LoginRequest): LoginResponse {
        log.info("Getting user login data")

        val tokenResponse = getToken(loginRequest.code)

        log.info("Got token for user ${tokenResponse!!.access_token}")

        val emailsResponse = getUserEmails(tokenResponse)

        log.info("Got user emails ${emailsResponse.size}")

        val accessRights = checkUserAccessRights(emailsResponse.first().email)

        saveToken(tokenResponse.access_token, emailsResponse.first().email)

        return LoginResponse(tokenResponse.access_token, emailsResponse.first().email, accessRights)
    }

    private fun getToken(code: String): GithubAuthResponse? {
        val githubAuthRequest = GithubAuthRequest(
                System.getenv("TEST_PLATFORM_CLIENT_ID"),
                System.getenv("TEST_PLATFORM_SECRET"),
                code)

        val rt = RestTemplate()

        return rt.postForObject(ACCESS_TOKEN_URI, githubAuthRequest, GithubAuthResponse::class.java)
    }

    private fun getUserEmails(tokenResponse: GithubAuthResponse): List<EmailDao> {
        val rt = RestTemplate()

        val headers = HttpHeaders()
        headers.set("Authorization", "token ${tokenResponse.access_token}")

        val entity = HttpEntity<MultiValueMap<String, String>>(headers)

        val response = rt.exchange(
                USER_EMAILS_URI,
                HttpMethod.GET, entity,
                object : ParameterizedTypeReference<List<EmailDao>>() {})

        return response.body ?: emptyList()
    }

    private fun checkUserAccessRights(email: String): String {
        val user = usersRepository.findByName(email)

        if (user == null) {
            log.warn("User with email: $email doesn't exist")
        }

        if (user!!.isStudent) {
            return "student"
        } else {
            return "lecturer"
        }
    }

    private fun saveToken(access_token: String, email: String) {

        val user = usersRepository.findByName(email)

        if (user == null) {
            log.warn("User with email: $email doesn't exist")
            return
        }

        usersAuthRepository.save(UserAuth(token = access_token, user = user))
    }

    companion object {
        val log = LoggerFactory.getLogger(LoginService::class.java)
        const val ACCESS_TOKEN_URI = "https://github.com/login/oauth/access_token"
        const val USER_EMAILS_URI = "https://api.github.com/user/emails"
    }
}