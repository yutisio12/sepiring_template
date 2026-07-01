package com.sepring.template.controller

import com.sepring.template.security.JwtService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class TokenRequest(val input: String)
data class TokenResponse(val token: String)
data class ValidateRequest(val token: String)
data class ValidateResponse(
    val valid: Boolean,
    val subject: String?,
    val input: String?,
    val issuedAt: String?,
    val expiration: String?
)

@RestController
@RequestMapping("/api/auth")
class JwtController(private val jwtService: JwtService) {

    @PostMapping("/generate-token")
    fun generateToken(@RequestBody request: TokenRequest): TokenResponse {
        val jwt = jwtService.generateToken(request.input)
        return TokenResponse("Bearer $jwt")
    }

    @PostMapping("/validate-token")
    fun validateToken(@RequestBody request: ValidateRequest): ValidateResponse {
        val token = request.token.removePrefix("Bearer ").trim()
        val valid = jwtService.validateToken(token)
        if (!valid) return ValidateResponse(false, null, null, null, null)
        val claims = jwtService.parseToken(token).payload
        val decryptedInput = jwtService.getDecryptedInput(token)
        return ValidateResponse(
            valid = true,
            subject = claims.subject,
            input = decryptedInput,
            issuedAt = claims.issuedAt?.toInstant()?.toString(),
            expiration = claims.expiration?.toInstant()?.toString()
        )
    }
}
