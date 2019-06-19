package com.drabarz.karolina.testplatformrunner

import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.context.TestComponent
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.util.MultiValueMap
import org.springframework.http.HttpEntity
import org.springframework.util.LinkedMultiValueMap
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = [TestPlatformRunnerApplicationTests.PathProvImplTestContextConfiguration::class])
class TestPlatformRunnerApplicationTests {

    @TestConfiguration
    @Import(TestPlatformRunnerApplication::class)
    class PathProvImplTestContextConfiguration {

        @Bean
        @Primary
        fun pathProv(): PathProv {
            return TestPathProvider()
        }
    }

	@Autowired
	private val restTemplate: TestRestTemplate? = null

	@After
	fun clean() {
		Files.walk(Paths.get(TestPathProvider().projectsPath))
				.sorted(Comparator.reverseOrder())
				.map(Path::toFile)
				.forEach(::println)
	}

	@Test
	fun shouldCreateProject() {
		val headers = HttpHeaders()
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED)

		val map = LinkedMultiValueMap<String, String>()
		map.add("projectName", "test_project")

		val request = HttpEntity<MultiValueMap<String, String>>(map, headers)

		val body = this.restTemplate!!.postForObject("/project", request, Object::class.java)
		println(body)
	}
}

@TestComponent
class TestPathProvider: PathProv {
	private final val pathPrefix = "/home/karolina/MGR/test/platform"
	final override val jarPath = "$pathPrefix/jars"
	final override val projectsPath = "$pathPrefix/projects"
}