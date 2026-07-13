package com.sepring.template.controller.v1

import com.sepring.template.model.FileRecord
import com.sepring.template.service.FileService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

data class FileResponse(
    val id: Long,
    val name: String,
    val originalName: String,
    val mimeType: String?,
    val sizeBytes: Long,
    val storageUrl: String,
    val storageType: String,
    val createdAt: String?
)

private fun FileRecord.toResponse() = FileResponse(
    id = id,
    name = name,
    originalName = originalName,
    mimeType = mimeType,
    sizeBytes = sizeBytes,
    storageUrl = storageUrl,
    storageType = storageType,
    createdAt = createdAt.toString()
)

@RestController
@RequestMapping("/api/v1/files")
@Tag(name = "Files", description = "File upload and management")
@SecurityRequirement(name = "bearer-jwt")
class FileController(
    private val fileService: FileService
) {
    @PostMapping("/upload")
    @Operation(summary = "Upload a file")
    fun upload(
        @RequestParam("file") file: MultipartFile,
        auth: Authentication?
    ): FileResponse {
        val userId = if (auth?.name != null) {
            try { auth.name.toLong() } catch (_: Exception) { null }
        } else null
        return fileService.upload(file, userId).toResponse()
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get file by ID")
    fun findById(@PathVariable id: Long): FileResponse =
        fileService.findById(id).toResponse()

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a file")
    fun delete(@PathVariable id: Long) = fileService.delete(id)
}
