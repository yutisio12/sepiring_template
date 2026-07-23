package com.sepring.template.service

import com.sepring.template.model.User
import com.sepring.template.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun register(username: String, rawPassword: String): User {
        require(!userRepository.existsByUsername(username)) { "Username already taken" }
        val user = User(
            username = username,
            password = passwordEncoder.encode(rawPassword)!!,
            role = "USER"
        )
        return userRepository.save(user)
    }

    fun findByUsername(username: String): User =
        userRepository.findByUsername(username)
            .orElseThrow { NoSuchElementException("User not found: $username") }

    fun findById(id: Long): User =
        userRepository.findById(id)
            .orElseThrow { NoSuchElementException("User not found: $id") }

    fun existsByUsername(username: String): Boolean =
        userRepository.existsByUsername(username)
}
