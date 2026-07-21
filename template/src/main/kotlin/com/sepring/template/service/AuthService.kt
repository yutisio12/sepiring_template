package com.sepring.template.service

import com.sepring.template.model.User
import com.sepring.template.security.JwtService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

data class AuthResult(
    val accessToken: String,
    val refreshToken: String,
    val expiresInMs: Long
)

data class RegisterRequest(
    val username: String,
    val password: String
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class RefreshRequest(
    val refreshToken: String
)

@Service
@Transactional
class AuthService(
    private val userService: UserService,
    private val jwtService: JwtService,
    private val passwordEncoder: PasswordEncoder
) {
    fun register(req: RegisterRequest): AuthResult {
        val user = userService.register(req.username, req.password)
        return generateTokens(user)
    }

    fun login(req: LoginRequest): AuthResult {
        val user = userService.findByUsername(req.username)
        require(passwordEncoder.matches(req.password, user.password)) { "Invalid credentials" }
        return generateTokens(user)
    }

    fun refresh(req: RefreshRequest): AuthResult {
        val jws = jwtService.parseRefreshToken(req.refreshToken)
        val username = jws.payload.subject ?: throw IllegalArgumentException("Invalid refresh token")
        val user = userService.findByUsername(username)
        return generateTokens(user)
    }

    private fun generateTokens(user: User): AuthResult {
        val accessToken = jwtService.generateAccessToken(user.username, user.role)
        val refreshToken = jwtService.generateRefreshToken(user.username, user.role)
        return AuthResult(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresInMs = 86400000
        )
    }
}
