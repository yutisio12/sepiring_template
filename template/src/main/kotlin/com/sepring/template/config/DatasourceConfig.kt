package com.sepring.template.config

import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import javax.sql.DataSource

@Configuration
class DatasourceConfig {

    @Primary
    @Bean
    fun primaryDataSource(properties: DataSourceProperties): DataSource =
        properties.initializeDataSourceBuilder().build()

    @Bean
    @ConfigurationProperties(prefix = "app.datasource.secondary")
    fun secondaryDataSource(): DataSource =
        DataSourceBuilder.create().build()
}
