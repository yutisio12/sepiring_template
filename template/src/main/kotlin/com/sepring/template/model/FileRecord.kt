package com.sepring.template.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "files")
class FileRecord(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false, length = 500)
    var name: String = "",

    @Column(nullable = false, length = 500)
    var originalName: String = "",

    @Column(length = 100)
    var mimeType: String? = null,

    @Column(nullable = false)
    var sizeBytes: Long = 0,

    @Column(nullable = false, columnDefinition = "TEXT")
    var storageUrl: String = "",

    @Column(nullable = false, length = 20)
    var storageType: String = "LOCAL",

    var uploadedBy: Long? = null,

    @Column(nullable = false)
    var createdAt: Instant = Instant.now()
)
