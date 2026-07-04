package com.sepring.template.security

import io.github.bucket4j.Bucket
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

@Component
class RateLimitFilter(
    @Value("\${app.rate-limit.public.capacity:100}") private val publicCapacity: Long,
    @Value("\${app.rate-limit.public.refill-per-minute:100}") private val publicRefill: Long,
    @Value("\${app.rate-limit.authenticated.capacity:60}") private val authCapacity: Long,
    @Value("\${app.rate-limit.authenticated.refill-per-minute:60}") private val authRefill: Long
) : OncePerRequestFilter() {

    private val buckets = ConcurrentHashMap<String, Bucket>()

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI
        return path.startsWith("/swagger-ui") ||
                path.startsWith("/api-docs") ||
                path.startsWith("/h2-console") ||
                path == "/"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authentication = SecurityContextHolder.getContext().authentication
        val key: String
        val capacity: Long
        val refill: Long

        if (authentication != null && authentication.isAuthenticated && authentication.principal is String) {
            key = "auth:${authentication.principal}"
            capacity = authCapacity
            refill = authRefill
        } else {
            val ip = extractClientIp(request)
            key = "public:$ip"
            capacity = publicCapacity
            refill = publicRefill
        }

        val bucket = buckets.computeIfAbsent(key) {
            Bucket.builder()
                .addLimit { limit ->
                    limit.capacity(capacity)
                        .refillGreedy(refill, Duration.ofMinutes(1))
                }
                .build()
        }

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response)
        } else {
            response.status = HttpStatus.TOO_MANY_REQUESTS.value()
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.setHeader("Retry-After", "60")
            response.writer.write("""{"error":"Too Many Requests","message":"Rate limit exceeded. Try again in 60 seconds."}""")
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
