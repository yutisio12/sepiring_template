package com.sepring.template.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Base64
import java.util.Date

@Service
class JwtService(
    @Value("\${app.jwt.secret:}") private val jwtSecret: String,
    @Value("\${app.jwt.expiration:86400000}") private val expirationMs: Long,
    @Value("\${app.jwt.refresh-expiration:604800000}") private val refreshExpirationMs: Long,
    private val aesEcbUtil: AesEcbUtil
) {
    init {
        require(jwtSecret.isNotBlank()) { "JWT_SECRET environment variable is required" }
    }

    private val key by lazy { Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret)) }

    fun generateToken(rawInput: String): String {
        val encryptedInput = aesEcbUtil.encryptToBase64Url(rawInput)
        val now = Date()
        val expiration = Date(now.time + expirationMs)
        return Jwts.builder()
            .claim("encryptedInput", encryptedInput)
            .subject(rawInput)
            .issuedAt(now)
            .expiration(expiration)
            .signWith(key)
            .compact()
    }

    fun generateAccessToken(username: String, role: String): String {
        val now = Date()
        val expiration = Date(now.time + expirationMs)
        return Jwts.builder()
            .claim("type", "access")
            .claim("role", role)
            .subject(username)
            .issuedAt(now)
            .expiration(expiration)
            .signWith(key)
            .compact()
    }

    fun generateRefreshToken(username: String, role: String): String {
        val now = Date()
        val expiration = Date(now.time + refreshExpirationMs)
        return Jwts.builder()
            .claim("type", "refresh")
            .claim("role", role)
            .subject(username)
            .issuedAt(now)
            .expiration(expiration)
            .signWith(key)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            parseToken(token)
            true
        } catch (_: JwtException) {
            false
        }
    }

    fun parseToken(token: String): Jws<Claims> {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
    }

    fun parseRefreshToken(token: String): Jws<Claims> {
        val claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
        require(claims.payload.get("type", String::class.java) == "refresh") { "Not a refresh token" }
        return claims
    }

    fun getDecryptedInput(token: String): String {
        val claims = parseToken(token).payload
        val encryptedInput = claims.get("encryptedInput", String::class.java)
        return aesEcbUtil.decryptBase64Url(encryptedInput)
    }

    fun getUsernameFromToken(token: String): String {
        return parseToken(token).payload.subject
    }

    fun getRoleFromToken(token: String): String {
        return parseToken(token).payload.get("role", String::class.java)
    }
}
