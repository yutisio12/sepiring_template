package com.sepring.template.service.file

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "app.file-storage")
data class FileStorageConfig(
    var type: String = "s3",
    var s3: S3Config = S3Config(),
    var sftp: SftpConfig = SftpConfig(),
    var ftp: FtpConfig = FtpConfig()
) {
    data class S3Config(
        var endpoint: String = "",
        var bucket: String = "",
        var accessKey: String = "",
        var secretKey: String = ""
    )

    data class SftpConfig(
        var host: String = "localhost",
        var port: Int = 22,
        var username: String = "",
        var password: String = ""
    )

    data class FtpConfig(
        var host: String = "localhost",
        var port: Int = 21,
        var username: String = "",
        var password: String = ""
    )
}
