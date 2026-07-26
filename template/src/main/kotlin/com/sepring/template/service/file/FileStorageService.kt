package com.sepring.template.service.file

import org.springframework.web.multipart.MultipartFile

interface FileStorageService {
    fun upload(file: MultipartFile): String
    fun delete(storageUrl: String)
}
