package com.github.prime.utils.color

import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.roundToInt

/**
 * 마인크래프트 기본 색상 및 스타일 코드를 표현합니다.
 *
 * @property code 입력 식별자로 사용되는 문자 (예: 'a')
 * @property colorChar 실제 렌더링에 사용되는 마인크래프트 포맷 문자열 (예: "§a")
 */
enum class ColorCode(val code: Char, val colorChar: String) {
    BLACK('0', "§0"),
    DARK_BLUE('1', "§1"),
    DARK_GREEN('2', "§2"),
    DARK_AQUA('3', "§3"),
    DARK_RED('4', "§4"),
    DARK_PURPLE('5', "§5"),
    GOLD('6', "§6"),
    GRAY('7', "§7"),
    DARK_GRAY('8', "§8"),
    BLUE('9', "§9"),
    GREEN('a', "§a"),
    AQUA('b', "§b"),
    RED('c', "§c"),
    LIGHT_PURPLE('d', "§d"),
    YELLOW('e', "§e"),
    WHITE('f', "§f"),

    OBFUSCATED('k', "§k"),
    BOLD('l', "§l"),
    STRIKETHROUGH('m', "§m"),
    UNDERLINE('n', "§n"),
    ITALIC('o', "§o"),
    RESET('r', "§r");

    override fun toString(): String = colorChar

    companion object {
        private val BY_CODE = entries.associateBy { it.code }

        fun getByCode(code: Char): ColorCode? {
            return BY_CODE[code]
        }
    }
}


object ColorUtils {
    private const val ALT_COLOR_CHAR = '&'
    private const val COLOR_CHAR = '§'

    private val HEX_PATTERN: Pattern = Pattern.compile("<#([A-Fa-f0-9]{6})>", Pattern.CASE_INSENSITIVE)
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
        var gradMatcher = GRADIENT_PATTERN.matcher(translated)
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
            hexMatcher.appendReplacement(hexBuffer, convertToMcHex(hexCode))
        }
        hexMatcher.appendTail(hexBuffer)
        translated = hexBuffer.toString()

        // LEGACY
        val b = translated.toCharArray()
        val length = b.size
        var i = 0
        while (i < length - 1) {
            if (b[i] == ALT_COLOR_CHAR) {
                val color = ColorCode.getByCode(b[i + 1])
                if (color != null) {
                    b[i] = COLOR_CHAR
                    b[i + 1] = color.code.lowercaseChar()
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
        if (text.length == 1) return convertToMcHex(startHex) + text

        val startRgb = hexToRgb(startHex)
        val endRgb = hexToRgb(endHex)
        val result = StringBuilder()
        val stepCount = text.length - 1

        for (i in text.indices) {
            val ratio = i.toFloat() / stepCount

            val r = (startRgb[0] + ratio * (endRgb[0] - startRgb[0])).roundToInt()
            val g = (startRgb[1] + ratio * (endRgb[1] - startRgb[1])).roundToInt()
            val b = (startRgb[2] + ratio * (endRgb[2] - startRgb[2])).roundToInt()

            val currentHex = String.format("%02x%02x%02x", r, g, b)

            result.append(convertToMcHex(currentHex))
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
     * 일반 HEX 문자열을 마인크래프트 내부 HEX 포맷으로 변환합니다.
     *
     * @param hex 변환할 HEX 문자열
     * @return 마인크래프트 HEX 포맷 문자열
     */
    private fun convertToMcHex(hex: String): String {
        val builder = StringBuilder("${COLOR_CHAR}x")
        for (char in hex.lowercase()) {
            builder.append(COLOR_CHAR).append(char)
        }
        return builder.toString()
    }

    /**
     * 문자열 내에 존재하는 모든 색상 및 스타일 코드를 제거합니다.
     *
     * @param text 원본 문자열
     * @return 순수 텍스트
     */
    fun stripColor(text: String?): String {
        if (text.isNullOrEmpty()) return ""

        val legacyPattern = "§[0-9a-fk-orA-FK-OR]".toRegex()
        val hexPattern = "(?i)§x(§[0-9a-f]){6}".toRegex()

        return text.replace(hexPattern, "").replace(legacyPattern, "")
    }
}

/**
 * 문자열에 포함된 컬러(&, Hex, Gradient)를 마인크래프트 포맷으로 변환합니다.
 *
 * val msg1 = "&fHello World!".colorize()
 * val msg2 = "<#FF5555>Hello World!".colorize()
 * val msg3 = "<gradient:#FF0000:#000000>Hello World!</gradient>".colorize()
 *
 */
fun String.colorize(): String = ColorUtils.translate(this)
fun String.stripColor(): String = ColorUtils.stripColor(this)