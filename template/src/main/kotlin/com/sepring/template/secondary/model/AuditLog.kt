package com.sepring.template.secondary.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "audit_logs")
class AuditLog(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false, length = 100)
    var action: String = "",

    @Column(length = 100)
    var entityType: String? = null,

    @Column(length = 100)
    var entityId: String? = null,

    @Column(columnDefinition = "TEXT")
    var details: String? = null,

    @Column(length = 100)
    var performedBy: String? = null,

    @Column(nullable = false)
    var createdAt: Instant = Instant.now()
)
