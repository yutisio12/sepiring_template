package com.sepring.template.integration

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.sql.DataSource

@SpringBootTest
class HealthIntegrationTest {

    @Autowired
    private lateinit var primaryDataSource: DataSource

    @Autowired
    private lateinit var secondaryDataSource: DataSource

    @Test
    fun `primary database should be reachable`() {
        try {
            val conn = primaryDataSource.connection
            assert(conn.isValid(2)) { "Primary DB connection should be valid" }
            conn.close()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    @Test
    fun `secondary database should be reachable`() {
        try {
            val conn = secondaryDataSource.connection
            assert(conn.isValid(2)) { "Secondary DB connection should be valid" }
            conn.close()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}
