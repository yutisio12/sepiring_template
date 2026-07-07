package com.sepring.template.config

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import tools.jackson.core.json.JsonFactory
import tools.jackson.databind.json.JsonMapper

@Configuration
class JacksonConfig {

    @Bean
    @Scope("prototype")
    fun jsonMapperBuilder(customizers: List<JsonMapperBuilderCustomizer>): JsonMapper.Builder {
        val factory = JsonFactory.builder()
            .characterEscapes(HtmlCharacterEscapes())
            .build()
        val builder = JsonMapper.builder(factory)
        customizers.forEach { it.customize(builder) }
        return builder
    }
}
