package com.sepring.template.config

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import tools.jackson.databind.ObjectMapper

@SpringBootTest
class JacksonHtmlEscapeTest {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `HTML characters should be escaped in JSON output`() {
        val input = mapOf("name" to "<script>alert('xss')</script> &")
        val json = objectMapper.writeValueAsString(input)
        assertThat(json).contains("&lt;")
        assertThat(json).contains("&gt;")
        assertThat(json).contains("&amp;")
        assertThat(json).doesNotContain("<script>")
    }
}
