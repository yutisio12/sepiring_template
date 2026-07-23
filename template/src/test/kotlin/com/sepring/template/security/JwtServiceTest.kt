package com.sepring.template.security

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class JwtServiceTest {

    private lateinit var jwtService: JwtService
    private lateinit var aesEcbUtil: AesEcbUtil

    @BeforeEach
    fun setup() {
        aesEcbUtil = AesEcbUtil("ccooqIkjqKNSGgtWARM9iN54152lG/En44az0q8wSjo=")
        jwtService = JwtService(
            jwtSecret = "8BnlLtbjl/FhbMGpF+duOoIlskRlujEMSa952uGhAdY=",
            expirationMs = 86400000,
            refreshExpirationMs = 604800000,
            aesEcbUtil = aesEcbUtil
        )
    }

    @Test
    fun `generate token should produce non-empty string`() {
        val token = jwtService.generateToken("test-input")
        assertThat(token).isNotBlank()
    }

    @Test
    fun `generate and validate valid token should return true`() {
        val token = jwtService.generateToken("test-input")
        assertThat(jwtService.validateToken(token)).isTrue()
    }

    @Test
    fun `validate invalid token should return false`() {
        assertThat(jwtService.validateToken("invalid-token")).isFalse()
    }

    @Test
    fun `get decrypted input should return original`() {
        val original = "secret-123"
        val token = jwtService.generateToken(original)
        assertThat(jwtService.getDecryptedInput(token)).isEqualTo(original)
    }

    @Test
    fun `generate access token should contain type claim`() {
        val token = jwtService.generateAccessToken("user1", "ADMIN")
        val claims = jwtService.parseToken(token).payload
        assertThat(claims.get("type", String::class.java)).isEqualTo("access")
        assertThat(claims.get("role", String::class.java)).isEqualTo("ADMIN")
        assertThat(claims.subject).isEqualTo("user1")
    }

    @Test
    fun `generate refresh token should contain type claim`() {
        val token = jwtService.generateRefreshToken("user1", "USER")
        val claims = jwtService.parseRefreshToken(token).payload
        assertThat(claims.get("type", String::class.java)).isEqualTo("refresh")
        assertThat(claims.subject).isEqualTo("user1")
    }

    @Test
    fun `validate refresh token should reject access token`() {
        val token = jwtService.generateAccessToken("user1", "USER")
        assertThrows<IllegalArgumentException> {
            jwtService.parseRefreshToken(token)
        }
    }
}
