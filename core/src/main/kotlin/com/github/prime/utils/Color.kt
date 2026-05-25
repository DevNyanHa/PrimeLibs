package com.github.prime.utils

import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.roundToInt
import kotlin.text.iterator

/**
 * 마인크래프트에서 사용되는 모든 색상 및 스타일 정보를 표현하는 클래스
 */
sealed class Color {
    /**
     * 마인크래프트 클라이언트가 인식할 수 있는 내부 포맷 문자열을 반환합니다.
     *
     * @return `§` 문자로 결합된 마인크래프트 내부 표준 포맷 문자열
     */
    abstract fun toFormat(): String

    protected fun buildHex(hex: String): String {
        val builder = StringBuilder("${ColorUtils.COLOR_CHAR}x")
        for (char in hex) {
            builder.append(ColorUtils.COLOR_CHAR).append(char)
        }
        return builder.toString()
    }

    data class Legacy(val code: Char) : Color() {
        override fun toFormat(): String = "${ColorUtils.COLOR_CHAR}$code"
    }

    data class Hex(val hexCode: String) : Color() {
        override fun toFormat(): String = buildHex(hexCode.lowercase())
    }

    data class Rgb(val r: Int, val g: Int, val b: Int) : Color() {
        override fun toFormat(): String {
            val hexStr = String.format("%02x%02x%02x", r, g, b)
            return buildHex(hexStr)
        }
    }
}


object ColorUtils {
    const val ALT_COLOR_CHAR = '&'
    const val COLOR_CHAR = '§'

    private val HEX_PATTERN = Pattern.compile("<#([A-Fa-f0-9]{6})>", Pattern.CASE_INSENSITIVE)
    private val GRADIENT_PATTERN = Pattern.compile("<gradient:#([A-Fa-f0-9]{6}):#([A-Fa-f0-9]{6})>(.*?)</gradient>", Pattern.CASE_INSENSITIVE)

    /**
     * 문자열에 포함된 GRADIENT, HEX, LEGACY 컬러 코드를 일괄 치환합니다.
     *
     * @param text 변환할 원본 문자열
     * @return 변환된 문자열
     */
    fun translate(text: String?): String {
        if (text.isNullOrEmpty()) return ""

        var translated = text

        // GRADIENT
        val gradMatcher = GRADIENT_PATTERN.matcher(translated)
        val gradBuffer = StringBuffer()
        while (gradMatcher.find()) {
            val startHex = gradMatcher.group(1)
            val endHex = gradMatcher.group(2)
            val content = gradMatcher.group(3)

            val gradientText = createGradient(content, startHex, endHex)
            gradMatcher.appendReplacement(gradBuffer, Matcher.quoteReplacement(gradientText))
        }
        gradMatcher.appendTail(gradBuffer)
        translated = gradBuffer.toString()

        // HEX
        val hexMatcher = HEX_PATTERN.matcher(translated)
        val hexBuffer = StringBuffer()
        while (hexMatcher.find()) {
            val hexCode = hexMatcher.group(1)
            val hexColor = Color.Hex(hexCode)
            hexMatcher.appendReplacement(hexBuffer, hexColor.toFormat())
        }
        hexMatcher.appendTail(hexBuffer)
        translated = hexBuffer.toString()

        // LEGACY
        val b = translated.toCharArray()
        val length = b.size
        var i = 0
        while (i < length - 1) {
            if (b[i] == ALT_COLOR_CHAR) {
                val nextChar = b[i + 1].lowercaseChar()
                if ("0123456789abcdefklmnor".indexOf(nextChar) > -1) {
                    val legacyColor = Color.Legacy(nextChar)
                    val formatted = legacyColor.toFormat()
                    b[i] = formatted[0]
                    b[i + 1] = formatted[1]
                    i++
                }
            }
            i++
        }

        return String(b)
    }

    /**
     * 두 HEX 색상 사이의 선형 보간을 통해 문자별 그라데이션 문자열을 생성합니다.
     *
     * @param text 그라데이션을 적용할 대상 문자열
     * @param startHex 시작 HEX
     * @param endHex 종료 HEX
     * @return 포맷이 적용된 문자열
     */
    private fun createGradient(text: String, startHex: String, endHex: String): String {
        if (text.isEmpty()) return ""
        if (text.length == 1) return Color.Hex(startHex).toFormat() + text

        val startRgb = hexToRgb(startHex)
        val endRgb = hexToRgb(endHex)
        val result = StringBuilder()
        val stepCount = text.length - 1

        for (i in text.indices) {
            val ratio = i.toFloat() / stepCount

            val r = (startRgb[0] + ratio * (endRgb[0] - startRgb[0])).roundToInt()
            val g = (startRgb[1] + ratio * (endRgb[1] - startRgb[1])).roundToInt()
            val b = (startRgb[2] + ratio * (endRgb[2] - startRgb[2])).roundToInt()

            val rgbColor = Color.Rgb(r, g, b)
            result.append(rgbColor.toFormat())
            result.append(text[i])
        }

        return result.toString()
    }

    /**
     * 6자리 HEX 문자열을 RGB 정수 배열로 변환합니다.
     *
     * @param hex 6자리 헥사 문자열
     * @return [R, G, B] 정수 배열
     */
    private fun hexToRgb(hex: String): IntArray {
        val value = hex.toInt(16)
        return intArrayOf((value shr 16) and 0xFF, (value shr 8) and 0xFF, value and 0xFF)
    }

    /**
     * 문자열 내에 존재하는 모든 색상 및 스타일 코드를 제거합니다.
     *
     * @param text 원본 문자열
     * @return 순수 텍스트
     */
    fun stripColor(text: String?): String {
        if (text.isNullOrEmpty()) return ""

        val legacyPattern = "$COLOR_CHAR[0-9a-fk-orA-FK-OR]".toRegex()
        val hexPattern = "(?i)${COLOR_CHAR}x(${COLOR_CHAR}[0-9a-f]){6}".toRegex()

        return text.replace(hexPattern, "").replace(legacyPattern, "")
    }
}

/**
 * 문자열에 포함된 컬러(&, Hex, Gradient)를 마인크래프트 포맷으로 변환합니다.
 *
 * ```
 * val msg1 = "&fHello World!".colorize()
 * val msg2 = "<#FF5555>Hello World!".colorize()
 * val msg3 = "<gradient:#FF0000:#000000>Hello World!</gradient>".colorize()
 * ```
 */
fun String.colorize(): String = ColorUtils.translate(this)

/**
 * 문자열 내에 존재하는 모든 마인크래프트 내장 색상 및 스타일 식별 문자를 제거합니다.
 *
 * ```
 * val msg = "§fHello World!".stripColor()
 * ```
 */
fun String.stripColor(): String = ColorUtils.stripColor(this)