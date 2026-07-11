package com.sepring.template.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.header.writers.StaticHeadersWriter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val rateLimitFilter: RateLimitFilter
) {

    @Bean
    @Order(1)
    fun swaggerSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .securityMatcher("/swagger-ui/**", "/api-docs/**", "/h2-console/**", "/")
            .csrf { it.disable() }
            .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter::class.java)
            .authorizeHttpRequests { auth -> auth.anyRequest().permitAll() }
            .headers { headers ->
                headers
                    .xssProtection { xss -> xss.headerValue(org.springframework.security.web.header.writers.XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK) }
                    .contentSecurityPolicy { csp ->
                        csp.policyDirectives("default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval'; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self' data:; frame-ancestors 'none'")
                    }
                    .referrerPolicy { referrer -> referrer.policy(org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER) }
                    .frameOptions { frame -> frame.sameOrigin() }
            }
        return http.build()
    }

    @Bean
    @Order(2)
    fun apiSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .securityMatcher("/api/**")
            .csrf { it.disable() }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter::class.java)
            .authorizeHttpRequests { auth ->
                auth.requestMatchers("/api/auth/**").permitAll()
                auth.anyRequest().authenticated()
            }
            .headers { headers ->
                headers
                    .xssProtection { xss -> xss.headerValue(org.springframework.security.web.header.writers.XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK) }
                    .contentSecurityPolicy { csp ->
                        csp.policyDirectives("default-src 'self'; script-src 'self'; style-src 'self'; img-src 'self' data:; font-src 'self' data:; frame-ancestors 'none'")
                    }
                    .referrerPolicy { referrer -> referrer.policy(org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER) }
                    .frameOptions { frame -> frame.deny() }
                    .httpStrictTransportSecurity { hsts ->
                        hsts.maxAgeInSeconds(31536000).includeSubDomains(true)
                    }
                    .contentTypeOptions { }
                    .cacheControl { }
                    .addHeaderWriter(StaticHeadersWriter("Permissions-Policy", "geolocation=(), microphone=(), camera=(), payment=(), usb=(), interest-cohort=()"))
            }
        return http.build()
    }

    @Bean
    @Order(3)
    fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter::class.java)
            .authorizeHttpRequests { auth -> auth.anyRequest().denyAll() }
            .headers { headers ->
                headers
                    .xssProtection { xss -> xss.headerValue(org.springframework.security.web.header.writers.XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK) }
                    .contentSecurityPolicy { csp ->
                        csp.policyDirectives("default-src 'self'; script-src 'self'; style-src 'self'; img-src 'self' data:; font-src 'self' data:; frame-ancestors 'none'")
                    }
                    .referrerPolicy { referrer -> referrer.policy(org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER) }
                    .frameOptions { frame -> frame.deny() }
                    .httpStrictTransportSecurity { hsts ->
                        hsts.maxAgeInSeconds(31536000).includeSubDomains(true)
                    }
                    .contentTypeOptions { }
                    .cacheControl { }
                    .addHeaderWriter(StaticHeadersWriter("Permissions-Policy", "geolocation=(), microphone=(), camera=(), payment=(), usb=(), interest-cohort=()"))
            }
        return http.build()
    }
}
