package com.sepring.template.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "users")
class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false, unique = true, length = 100)
    var username: String = "",

    @Column(nullable = false)
    var password: String = "",

    @Column(nullable = false, length = 20)
    var role: String = "USER"
) {
    var createdAt: Instant? = null
    var updatedAt: Instant? = null

    @PreUpdate
    fun onUpdate() {
        updatedAt = Instant.now()
    }
}
