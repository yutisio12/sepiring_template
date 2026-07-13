package com.sepring.template.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@Configuration
@EnableJpaRepositories(
    basePackages = ["com.sepring.template.repository"],
    entityManagerFactoryRef = "primaryEntityManagerFactory",
    transactionManagerRef = "primaryTransactionManager"
)
class DatasourceConfig(
    @Value("\${spring.datasource.url:jdbc:h2:mem:template_db}") private val url: String,
    @Value("\${spring.datasource.username:sa}") private val username: String,
    @Value("\${spring.datasource.password:}") private val password: String,
    @Value("\${spring.datasource.driver-class-name:org.h2.Driver}") private val driverClassName: String,
    @Value("\${spring.jpa.properties.hibernate.dialect:org.hibernate.dialect.H2Dialect}") private val dialect: String,
    @Value("\${spring.jpa.hibernate.ddl-auto:update}") private val ddlAuto: String
) {

    @Primary
    @Bean
    fun primaryDataSource(): DataSource =
        DataSourceBuilder.create()
            .url(url)
            .username(username)
            .password(password)
            .driverClassName(driverClassName)
            .build()

    @Primary
    @Bean
    fun primaryEntityManagerFactory(
        @Qualifier("primaryDataSource") dataSource: DataSource
    ): LocalContainerEntityManagerFactoryBean {
        val props = java.util.Properties().apply {
            setProperty("hibernate.hbm2ddl.auto", ddlAuto)
            setProperty("hibernate.dialect", dialect)
            setProperty("hibernate.format_sql", "true")
        }
        val emf = LocalContainerEntityManagerFactoryBean()
        emf.dataSource = dataSource
        emf.setPackagesToScan("com.sepring.template.model")
        emf.jpaVendorAdapter = HibernateJpaVendorAdapter()
        emf.setJpaProperties(props)
        return emf
    }

    @Primary
    @Bean
    fun primaryTransactionManager(
        @Qualifier("primaryEntityManagerFactory") emf: LocalContainerEntityManagerFactoryBean
    ): PlatformTransactionManager {
        return JpaTransactionManager(emf.`object`!!)
    }
}
