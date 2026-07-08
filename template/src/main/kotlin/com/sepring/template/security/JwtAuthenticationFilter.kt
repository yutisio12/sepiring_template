package com.sepring.template.security

import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService
) : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.removePrefix("Bearer ").trim()
            if (token.isNotEmpty()) {
                try {
                    if (jwtService.validateToken(token)) {
                        val claims = jwtService.parseToken(token).payload
                        val authentication = UsernamePasswordAuthenticationToken(
                            claims.subject,
                            null,
                            emptyList()
                        )
                        authentication.details = request.remoteAddr
                        SecurityContextHolder.getContext().authentication = authentication
                    }
                } catch (ex: JwtException) {
                    log.warn("Invalid JWT token: path={}, clientIp={}, error={}", request.requestURI, request.remoteAddr, ex.message)
                }
            }
        }

        filterChain.doFilter(request, response)
    }
}
