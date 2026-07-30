package com.sepring.template.service.file

import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

@Service
@ConditionalOnProperty(name = ["app.file-storage.type"], havingValue = "sftp")
class SftpFileStorageService(
    private val fileStorageConfig: FileStorageConfig
) : FileStorageService {

    private val log = LoggerFactory.getLogger(SftpFileStorageService::class.java)

    override fun upload(file: MultipartFile): String {
        val remoteDir = "/upload/${LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))}"
        val remoteFile = "$remoteDir/${UUID.randomUUID()}-${file.originalFilename}"

        var session: Session? = null
        var channel: ChannelSftp? = null
        try {
            val jsch = JSch()
            session = jsch.getSession(fileStorageConfig.sftp.username, fileStorageConfig.sftp.host, fileStorageConfig.sftp.port)
            session.setPassword(fileStorageConfig.sftp.password)
            session.setConfig("StrictHostKeyChecking", "no")
            session.connect(10000)

            channel = session.openChannel("sftp") as ChannelSftp
            channel.connect(5000)

            try { channel.cd(remoteDir) } catch (_: Exception) {
                channel.mkdir(remoteDir)
                channel.cd(remoteDir)
            }

            channel.put(file.inputStream, remoteFile)
            log.info("SFTP upload success: $remoteFile")
            return remoteFile
        } finally {
            channel?.disconnect()
            session?.disconnect()
        }
    }

    override fun delete(storageUrl: String) {
        var session: Session? = null
        var channel: ChannelSftp? = null
        try {
            val jsch = JSch()
            session = jsch.getSession(fileStorageConfig.sftp.username, fileStorageConfig.sftp.host, fileStorageConfig.sftp.port)
            session.setPassword(fileStorageConfig.sftp.password)
            session.setConfig("StrictHostKeyChecking", "no")
            session.connect(10000)

            channel = session.openChannel("sftp") as ChannelSftp
            channel.connect(5000)
            channel.rm(storageUrl)
            log.info("SFTP delete success: $storageUrl")
        } finally {
            channel?.disconnect()
            session?.disconnect()
        }
    }
}
