package com.sepring.template

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@TestPropertySource(properties = [
    "spring.datasource.url=jdbc:h2:mem:test_db",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "app.datasource.secondary.url=jdbc:h2:mem:test_secondary_db",
    "app.datasource.secondary.username=sa",
    "app.datasource.secondary.password=",
    "app.datasource.secondary.driver-class-name=org.h2.Driver"
])
class TemplateApplicationTests {
	@Test
	fun contextLoads() {
	}

}
