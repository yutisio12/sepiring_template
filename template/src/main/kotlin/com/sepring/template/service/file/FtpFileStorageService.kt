package com.sepring.template.service.file

import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

@Service
@ConditionalOnProperty(name = ["app.file-storage.type"], havingValue = "ftp")
class FtpFileStorageService(
    private val fileStorageConfig: FileStorageConfig
) : FileStorageService {

    private val log = LoggerFactory.getLogger(FtpFileStorageService::class.java)

    override fun upload(file: MultipartFile): String {
        val remoteDir = "/upload/${LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))}"
        val remoteFile = "$remoteDir/${UUID.randomUUID()}-${file.originalFilename}"

        val ftp = FTPClient()
        try {
            ftp.connect(fileStorageConfig.ftp.host, fileStorageConfig.ftp.port)
            ftp.login(fileStorageConfig.ftp.username, fileStorageConfig.ftp.password)
            ftp.enterLocalPassiveMode()
            ftp.setFileType(FTP.BINARY_FILE_TYPE)

            try { ftp.makeDirectory(remoteDir) } catch (_: Exception) {}

            ftp.storeFile(remoteFile, file.inputStream)
            log.info("FTP upload success: $remoteFile")
            return remoteFile
        } finally {
            if (ftp.isConnected) ftp.disconnect()
        }
    }

    override fun delete(storageUrl: String) {
        val ftp = FTPClient()
        try {
            ftp.connect(fileStorageConfig.ftp.host, fileStorageConfig.ftp.port)
            ftp.login(fileStorageConfig.ftp.username, fileStorageConfig.ftp.password)
            ftp.deleteFile(storageUrl)
            log.info("FTP delete success: $storageUrl")
        } finally {
            if (ftp.isConnected) ftp.disconnect()
        }
    }
}
