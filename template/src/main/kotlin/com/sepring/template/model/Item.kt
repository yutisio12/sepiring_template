package com.sepring.template.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "items")
class Item(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var name: String = "",
    var description: String? = null
) {
    var createdAt: Instant? = null

    var updatedAt: Instant? = null

    @PreUpdate
    fun onUpdate() {
        updatedAt = Instant.now()
    }
}
