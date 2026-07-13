package com.sepring.template.controller.v1

import com.sepring.template.service.AuthService
import com.sepring.template.service.LoginRequest
import com.sepring.template.service.RefreshRequest
import com.sepring.template.service.RegisterRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class RegisterRequestDto(
    @field:NotBlank @field:Size(min = 3, max = 50)
    val username: String,
    @field:NotBlank @field:Size(min = 6, max = 100)
    val password: String
)

data class LoginRequestDto(
    @field:NotBlank
    val username: String,
    @field:NotBlank
    val password: String
)

data class RefreshRequestDto(
    @field:NotBlank
    val refreshToken: String
)

data class AuthResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val expiresInMs: Long,
    val tokenType: String = "Bearer"
)

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Register, login, and refresh JWT tokens")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    fun register(@Valid @RequestBody req: RegisterRequestDto): AuthResponseDto {
        val result = authService.register(RegisterRequest(req.username, req.password))
        return AuthResponseDto(result.accessToken, result.refreshToken, result.expiresInMs)
    }

    @PostMapping("/login")
    @Operation(summary = "Login with username and password")
    fun login(@Valid @RequestBody req: LoginRequestDto): AuthResponseDto {
        val result = authService.login(LoginRequest(req.username, req.password))
        return AuthResponseDto(result.accessToken, result.refreshToken, result.expiresInMs)
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token using refresh token")
    fun refresh(@Valid @RequestBody req: RefreshRequestDto): AuthResponseDto {
        val result = authService.refresh(RefreshRequest(req.refreshToken))
        return AuthResponseDto(result.accessToken, result.refreshToken, result.expiresInMs)
    }
}
