package com.sepring.template.security

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AesEcbUtilTest {

    private lateinit var aesEcbUtil: AesEcbUtil

    @BeforeEach
    fun setup() {
        aesEcbUtil = AesEcbUtil("ccooqIkjqKNSGgtWARM9iN54152lG/En44az0q8wSjo=")
    }

    @Test
    fun `encrypt and decrypt roundtrip should return original text`() {
        val original = "order-12345"
        val encrypted = aesEcbUtil.encryptToBase64Url(original)
        val decrypted = aesEcbUtil.decryptBase64Url(encrypted)
        assertThat(decrypted).isEqualTo(original)
    }

    @Test
    fun `encrypted output should be different from input`() {
        val original = "test-input"
        val encrypted = aesEcbUtil.encryptToBase64Url(original)
        assertThat(encrypted).isNotEqualTo(original)
    }

    @Test
    fun `encrypted output should be URL-safe`() {
        val original = "input with +/="
        val encrypted = aesEcbUtil.encryptToBase64Url(original)
        assertThat(encrypted).doesNotContain("+", "/", "=")
    }

    @Test
    fun `encrypt same input twice should produce different output`() {
        val original = "same-input"
        val encrypted1 = aesEcbUtil.encryptToBase64Url(original)
        val encrypted2 = aesEcbUtil.encryptToBase64Url(original)
        assertThat(encrypted1).isNotEqualTo(encrypted2)
    }
}
