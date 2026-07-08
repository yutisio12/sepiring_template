package com.sepring.template.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.security.SecureRandom

@Component
class AesEcbUtil(
    @Value("\${app.aes-cbc.key:}") private val aesKey: String
) {
    init {
        require(aesKey.isNotBlank()) { "AES_KEY_B64 environment variable is required" }
    }

    private val keySpec: SecretKeySpec by lazy {
        val keyBytes = Base64.getDecoder().decode(aesKey)
        SecretKeySpec(keyBytes, "AES")
    }

    private fun toBase64Url(b64: String): String =
        b64.replace("+", "-").replace("/", "_").replace("=", "")

    private fun base64UrlToBase64(b64url: String): String {
        var b64 = b64url.replace("-", "+").replace("_", "/")
        val pad = b64.length % 4
        if (pad == 2) b64 += "=="
        else if (pad == 3) b64 += "="
        return b64
    }

    fun encryptToBase64Url(plainText: String): String {
        val iv = ByteArray(16)
        SecureRandom().nextBytes(iv)

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, IvParameterSpec(iv))

        val ciphertext = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        val payload = iv + ciphertext
        val b64 = Base64.getEncoder().encodeToString(payload)

        return toBase64Url(b64)
    }

    fun decryptBase64Url(cipherTextB64Url: String): String {
        val b64 = base64UrlToBase64(cipherTextB64Url)
        val payload = Base64.getDecoder().decode(b64)

        val iv = payload.copyOfRange(0, 16)
        val ciphertext = payload.copyOfRange(16, payload.size)

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, keySpec, IvParameterSpec(iv))

        return String(cipher.doFinal(ciphertext), Charsets.UTF_8)
    }
}
