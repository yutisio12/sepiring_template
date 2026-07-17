package com.sepring.template.service

import com.sepring.template.model.FileRecord
import com.sepring.template.repository.FileRecordRepository
import com.sepring.template.service.file.FileStorageConfig
import com.sepring.template.service.file.FileStorageService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.Instant

@Service
@Transactional
class FileService(
    private val fileRecordRepository: FileRecordRepository,
    private val fileStorageService: FileStorageService,
    private val fileStorageConfig: FileStorageConfig
) {
    fun upload(file: MultipartFile, uploadedBy: Long?): FileRecord {
        val storageUrl = fileStorageService.upload(file)
        val record = FileRecord(
            name = file.originalFilename ?: "unknown",
            originalName = file.originalFilename ?: "unknown",
            mimeType = file.contentType,
            sizeBytes = file.size,
            storageUrl = storageUrl,
            storageType = fileStorageConfig.type.uppercase(),
            uploadedBy = uploadedBy,
            createdAt = Instant.now()
        )
        return fileRecordRepository.save(record)
    }

    fun findById(id: Long): FileRecord =
        fileRecordRepository.findById(id)
            .orElseThrow { NoSuchElementException("File not found: $id") }

    fun delete(id: Long) {
        val record = findById(id)
        fileStorageService.delete(record.storageUrl)
        fileRecordRepository.deleteById(id)
    }
}
