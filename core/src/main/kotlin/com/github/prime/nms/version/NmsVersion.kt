package com.github.prime.nms.version

/**
 * NMS 버전을 비교 가능한 값 객체로 표현합니다.
 *
 * @property major major 버전
 * @property minor minor 버전
 * @property patch patch 버전
 */
data class NmsVersion(
    val major: Int,
    val minor: Int,
    val patch: Int = 0
) : Comparable<NmsVersion> {
    /**
     * 두 [NmsVersion]을 비교합니다.
     *
     * @param other 비교할 [NmsVersion]
     * @return 음수: 낮음, 0: 같음, 양수: 높음
     */
    override fun compareTo(other: NmsVersion): Int =
        compareValuesBy(this, other, NmsVersion::major, NmsVersion::minor, NmsVersion::patch)

    /**
     * [NmsVersion]을 문자열 형태로 반환합니다.
     *
     * @return `major.minor.patch`
     */
    override fun toString(): String = "$major.$minor.$patch"

    companion object {
        /**
         * 문자열에서 숫자만 추출하여 [NmsVersion]객체를 생성합니다.
         *
         * @param raw 원본 문자열
         * @return 파싱된 [NmsVersion] 객체
         * @throws IllegalArgumentException
         * 버전을 파싱할 수 없는 경우 발생합니다.
         */
        fun parse(raw: String): NmsVersion {
            val parts =
                Regex("\\d+")
                    .findAll(raw)
                    .map { it.value.toInt() }
                    .toList()

            require(parts.size >= 2) {
                "Cannot parse version $raw"
            }

            return NmsVersion(
                major = parts[0],
                minor = parts[1],
                patch = parts.getOrElse(2) { 0 }
            )
        }
    }
}