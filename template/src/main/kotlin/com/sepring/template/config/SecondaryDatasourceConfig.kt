package com.sepring.template.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@Configuration
@EnableJpaRepositories(
    basePackages = ["com.sepring.template.secondary.repository"],
    entityManagerFactoryRef = "secondaryEntityManagerFactory",
    transactionManagerRef = "secondaryTransactionManager"
)
class SecondaryDatasourceConfig(
    @Value("\${app.datasource.secondary.url:jdbc:h2:mem:secondary_db}") private val url: String,
    @Value("\${app.datasource.secondary.username:sa}") private val username: String,
    @Value("\${app.datasource.secondary.password:}") private val password: String,
    @Value("\${app.datasource.secondary.driver-class-name:org.h2.Driver}") private val driverClassName: String,
    @Value("\${app.datasource.secondary.jpa.ddl-auto:update}") private val ddlAuto: String,
    @Value("\${app.datasource.secondary.jpa.dialect:org.hibernate.dialect.H2Dialect}") private val dialect: String
) {

    @Bean
    fun secondaryDataSource(): DataSource =
        DataSourceBuilder.create()
            .url(url)
            .username(username)
            .password(password)
            .driverClassName(driverClassName)
            .build()

    @Bean
    fun secondaryEntityManagerFactory(
        @Qualifier("secondaryDataSource") dataSource: DataSource
    ): LocalContainerEntityManagerFactoryBean {
        val props = java.util.Properties().apply {
            setProperty("hibernate.hbm2ddl.auto", ddlAuto)
            setProperty("hibernate.dialect", dialect)
            setProperty("hibernate.format_sql", "true")
        }
        val emf = LocalContainerEntityManagerFactoryBean()
        emf.dataSource = dataSource
        emf.setPackagesToScan("com.sepring.template.secondary.model")
        emf.jpaVendorAdapter = HibernateJpaVendorAdapter()
        emf.setJpaProperties(props)
        return emf
    }

    @Bean
    fun secondaryTransactionManager(
        @Qualifier("secondaryEntityManagerFactory") emf: LocalContainerEntityManagerFactoryBean
    ): PlatformTransactionManager {
        return JpaTransactionManager(emf.`object`!!)
    }
}
