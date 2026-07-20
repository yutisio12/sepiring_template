package com.sepring.template.secondary.repository

import com.sepring.template.secondary.model.AuditLog
import org.springframework.data.jpa.repository.JpaRepository

interface AuditLogRepository : JpaRepository<AuditLog, Long>
