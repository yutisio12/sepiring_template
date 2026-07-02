package com.sepring.template.controller

import com.sepring.template.security.JwtService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Schema(description = "Request body untuk generate token")
data class TokenRequest(
    @Schema(description = "Input bebas, text atau angka", example = "order-12345", required = true)
    val input: String
)

@Schema(description = "Response token JWT Bearer")
data class TokenResponse(
    @Schema(description = "JWT Bearer token", example = "Bearer eyJhbGciOiJIUzI1NiJ9...")
    val token: String
)

@Schema(description = "Request body untuk validasi token")
data class ValidateRequest(
    @Schema(description = "JWT Bearer token", example = "Bearer eyJhbGciOiJIUzI1NiJ9...", required = true)
    val token: String
)

@Schema(description = "Response hasil validasi token")
data class ValidateResponse(
    @Schema(description = "Apakah token valid", example = "true")
    val valid: Boolean,
    @Schema(description = "Subject dari token", nullable = true, example = "order-12345")
    val subject: String?,
    @Schema(description = "Input asli hasil decrypt AES-CBC", nullable = true, example = "order-12345")
    val input: String?,
    @Schema(description = "Waktu token dibuat (ISO-8601)", nullable = true, example = "2026-07-01T14:00:00Z")
    val issuedAt: String?,
    @Schema(description = "Waktu token kadaluarsa (ISO-8601)", nullable = true, example = "2026-07-02T14:00:00Z")
    val expiration: String?
)

@RestController
@RequestMapping("/api/auth")
@Tag(name = "JWT Token", description = "Generate dan validasi JWT Bearer token dengan enkripsi AES-CBC")
class JwtController(private val jwtService: JwtService) {

    @PostMapping("/generate-token")
    @Operation(
        summary = "Generate JWT Bearer token",
        description = "Menerima input bebas (text/angka), mengenkripsinya dengan AES-CBC, lalu mengembalikan JWT Bearer token yang signed HMAC-SHA256. Token URL-safe untuk dikirim via query parameter."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Token berhasil digenerate",
        content = [Content(schema = Schema(implementation = TokenResponse::class))]
    )
    fun generateToken(@RequestBody request: TokenRequest): TokenResponse {
        val jwt = jwtService.generateToken(request.input)
        return TokenResponse("Bearer $jwt")
    }

    @PostMapping("/validate-token")
    @Operation(
        summary = "Validasi dan decrypt JWT Bearer token",
        description = "Menerima JWT Bearer token, memverifikasi signature HMAC-SHA256, mendekripsi payload AES-CBC, dan mengembalikan isi token."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Token valid atau tidak",
        content = [Content(schema = Schema(implementation = ValidateResponse::class))]
    )
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
