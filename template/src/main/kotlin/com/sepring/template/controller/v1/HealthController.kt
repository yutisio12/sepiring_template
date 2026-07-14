package com.sepring.template.controller.v1

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import javax.sql.DataSource

data class DbHealthResponse(
    val status: String,
    val database: String? = null
)

data class HealthResponse(
    val status: String,
    val timestamp: String,
    val database: DbHealthResponse,
    val secondaryDatabase: DbHealthResponse
)

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Health", description = "Health check endpoints")
class HealthController {
    @Autowired
    private lateinit var primaryDataSource: DataSource

    @Autowired
    @Qualifier("secondaryDataSource")
    private lateinit var secondaryDataSource: DataSource

    @GetMapping("/health")
    @Operation(summary = "Health check API with database status")
    fun health(): HealthResponse {
        val primaryDb = try {
            val conn = primaryDataSource.connection
            val meta = conn.metaData
            val dbName = meta.databaseProductName
            conn.close()
            DbHealthResponse("UP", dbName)
        } catch (e: Exception) {
            DbHealthResponse("DOWN", null)
        }

        val secondaryDb = try {
            val conn = secondaryDataSource.connection
            val meta = conn.metaData
            val dbName = meta.databaseProductName
            conn.close()
            DbHealthResponse("UP", dbName)
        } catch (e: Exception) {
            DbHealthResponse("DOWN", null)
        }

        val overall = if (primaryDb.status == "UP") "UP" else "DOWN"

        return HealthResponse(
            status = overall,
            timestamp = Instant.now().toString(),
            database = primaryDb,
            secondaryDatabase = secondaryDb
        )
    }
}
