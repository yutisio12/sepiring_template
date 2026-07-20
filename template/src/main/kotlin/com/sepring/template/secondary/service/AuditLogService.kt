package com.sepring.template.secondary.service

import com.sepring.template.secondary.model.AuditLog
import com.sepring.template.secondary.repository.AuditLogRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
@Transactional("secondaryTransactionManager")
class AuditLogService(
    private val auditLogRepository: AuditLogRepository
) {
    fun log(action: String, entityType: String?, entityId: String?, details: String?, performedBy: String?): AuditLog {
        val auditLog = AuditLog(
            action = action,
            entityType = entityType,
            entityId = entityId,
            details = details,
            performedBy = performedBy,
            createdAt = Instant.now()
        )
        return auditLogRepository.save(auditLog)
    }

    fun findAll(): List<AuditLog> = auditLogRepository.findAll()
}
