package com.sepring.template.config

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.Instant

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    data class ErrorResponse(
        val error: String,
        val message: String,
        val timestamp: String = Instant.now().toString(),
        val path: String? = null
    )

    private fun buildResponse(status: HttpStatus, message: String, request: HttpServletRequest?): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            error = status.reasonPhrase,
            message = message,
            path = request?.requestURI
        )
        return ResponseEntity.status(status)
            .contentType(MediaType.APPLICATION_JSON)
            .body(response)
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(ex: NoSuchElementException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        log.warn("Resource not found: path={}, message={}", request.requestURI, ex.message)
        return buildResponse(HttpStatus.NOT_FOUND, "Resource tidak ditemukan", request)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val fieldErrors = ex.bindingResult.fieldErrors.joinToString("; ") { "${it.field}: ${it.defaultMessage}" }
        log.warn("Validation error: path={}, errors={}", request.requestURI, fieldErrors)
        return buildResponse(HttpStatus.BAD_REQUEST, "Validasi gagal: $fieldErrors", request)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleMalformedBody(ex: HttpMessageNotReadableException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        log.warn("Malformed request body: path={}, message={}", request.requestURI, ex.message)
        return buildResponse(HttpStatus.BAD_REQUEST, "Format request body tidak valid", request)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(ex: AccessDeniedException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        log.warn("Access denied: path={}, message={}", request.requestURI, ex.message)
        return buildResponse(HttpStatus.FORBIDDEN, "Akses ditolak", request)
    }

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthentication(ex: AuthenticationException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        log.info("Authentication failed: path={}, message={}", request.requestURI, ex.message)
        return buildResponse(HttpStatus.UNAUTHORIZED, "Autentikasi gagal", request)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneral(ex: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        log.error("Unhandled exception: path={}, method={}, message={}", request.requestURI, request.method, ex.message, ex)
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Terjadi kesalahan internal server", request)
    }
}
