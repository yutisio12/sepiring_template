package com.sepring.template.service.file

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.security.MessageDigest
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
@ConditionalOnProperty(name = ["app.file-storage.type"], havingValue = "s3")
class S3FileStorageService(
    private val fileStorageConfig: FileStorageConfig
) : FileStorageService {

    private val log = LoggerFactory.getLogger(S3FileStorageService::class.java)

    override fun upload(file: MultipartFile): String {
        val key = generateKey(file.originalFilename ?: "unknown")
        val bytes = file.bytes
        val url = "${fileStorageConfig.s3.endpoint}/${fileStorageConfig.s3.bucket}/$key"
        log.info("S3 upload to: $url (size=${bytes.size})")
        return url
    }

    override fun delete(storageUrl: String) {
        log.info("S3 delete: $storageUrl")
    }

    private fun generateKey(originalName: String): String {
        val date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
        val hash = MessageDigest.getInstance("MD5").digest((originalName + System.nanoTime()).toByteArray())
            .joinToString("") { "%02x".format(it) }
        return "$date/$hash-${originalName}"
    }
}
