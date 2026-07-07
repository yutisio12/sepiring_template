package com.sepring.template.config

import tools.jackson.core.SerializableString
import tools.jackson.core.io.CharacterEscapes
import tools.jackson.core.io.SerializedString

class HtmlCharacterEscapes : CharacterEscapes() {

    private val escapes: IntArray = run {
        val arr = IntArray(128)
        val standard = standardAsciiEscapesForJSON()
        System.arraycopy(standard, 0, arr, 0, standard.size)
        arr['<'.code] = ESCAPE_CUSTOM
        arr['>'.code] = ESCAPE_CUSTOM
        arr['&'.code] = ESCAPE_CUSTOM
        arr
    }

    override fun getEscapeCodesForAscii(): IntArray = escapes

    override fun getEscapeSequence(ch: Int): SerializableString = when (ch) {
        '<'.code -> SerializedString("&lt;")
        '>'.code -> SerializedString("&gt;")
        '&'.code -> SerializedString("&amp;")
        else -> throw IllegalArgumentException("No escape sequence for character $ch")
    }
}
