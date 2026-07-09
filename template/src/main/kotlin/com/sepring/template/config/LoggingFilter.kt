package com.sepring.template.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.UUID

@Component
@Order(1)
class LoggingFilter : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(LoggingFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val start = System.currentTimeMillis()
        val requestId = UUID.randomUUID().toString().subSequence(0, 8).toString()
        val clientIp = extractClientIp(request)

        try {
            MDC.put("requestId", requestId)
            MDC.put("method", request.method)
            MDC.put("path", request.requestURI)
            MDC.put("clientIp", clientIp)

            filterChain.doFilter(request, response)
        } finally {
            val duration = System.currentTimeMillis() - start
            val status = response.status

            val logEntry = mapOf(
                "requestId" to requestId,
                "method" to request.method,
                "path" to request.requestURI,
                "query" to (request.queryString ?: ""),
                "clientIp" to clientIp,
                "status" to status,
                "durationMs" to duration,
                "userAgent" to (request.getHeader("User-Agent") ?: "")
            )

            when {
                status >= 500 -> log.error("Request error: {}", logEntry)
                status >= 400 -> log.warn("Request client error: {}", logEntry)
                else -> log.info("Request ok: {}", logEntry)
            }

            MDC.clear()
        }
    }

    private fun extractClientIp(request: HttpServletRequest): String {
        val xff = request.getHeader("X-Forwarded-For")
        if (xff != null && xff.isNotBlank()) {
            return xff.split(",")[0].trim()
        }
        return request.remoteAddr ?: "unknown"
    }
}
