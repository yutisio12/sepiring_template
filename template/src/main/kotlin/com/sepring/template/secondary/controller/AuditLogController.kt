package com.sepring.template.secondary.controller

import com.sepring.template.secondary.model.AuditLog
import com.sepring.template.secondary.service.AuditLogService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class AuditLogRequest(
    val action: String,
    val entityType: String? = null,
    val entityId: String? = null,
    val details: String? = null
)

@RestController
@RequestMapping("/api/v1/audit-logs")
@Tag(name = "Audit Logs", description = "Audit logging module (secondary datasource)")
@SecurityRequirement(name = "bearer-jwt")
class AuditLogController(
    private val auditLogService: AuditLogService
) {
    @GetMapping
    @Operation(summary = "List all audit logs")
    fun findAll(): List<AuditLog> = auditLogService.findAll()

    @PostMapping
    @Operation(summary = "Create an audit log entry")
    fun create(@RequestBody request: AuditLogRequest, auth: Authentication?): AuditLog {
        val username = auth?.name
        return auditLogService.log(
            action = request.action,
            entityType = request.entityType,
            entityId = request.entityId,
            details = request.details,
            performedBy = username
        )
    }
}
