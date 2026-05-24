package com.github.prime.nms.version

data class NmsVersionRange(
    val minimum: NmsVersion,
    val maximum: NmsVersion? = null
) {
    /**
     * [NmsVersion]이 현재 범위에 포함되는지 확인합니다.
     *
     * @param version 확인할 [NmsVersion] 객체
     * @return 범위 내에 포함될 경우 true를 반환
     */
    operator fun contains(version: NmsVersion): Boolean {
        if (version < minimum) return false

        return maximum == null || version <= maximum
    }

    /**
     * [NmsVersionRange]를 문자열 형태로 반환합니다.
     *
     * @return `major.minor.patch-major.minor.patch` or `major.minor.patch+`
     */
    override fun toString(): String = maximum?.let { "$minimum - $it" } ?: "$minimum+"

    companion object {
        /**
         * 두 [NmsVersion]을 [NmsVersionRange]객체를 생성합니다.
         *
         * @param minimum 최소 [NmsVersion]
         * @param maximum 최대 [NmsVersion]
         * @return 생성된 [NmsVersionRange]객체
         */
        fun between(
            minimum: String,
            maximum: String
        ): NmsVersionRange =
            NmsVersionRange(
                minimum = NmsVersion.parse(minimum),
                maximum = NmsVersion.parse(maximum)
            )

        /**
         * 특정 major/minor 버전 라인의 [NmsVersionRange]를 생성합니다.
         *
         * @param major major 버전입니다.
         * @param minor minor 버전입니다.
         * @param minimumPatch 최소 patch 버전입니다. 기본값은 0입니다.
         * @param maximumPatch 최대 patch 버전입니다. 기본값은 99입니다.
         * @return 생성된 [NmsVersionRange] 객체
         */
        fun featureLine(
            major: Int,
            minor: Int,
            minimumPatch: Int = 0,
            maximumPatch: Int = 99
        ): NmsVersionRange =
            NmsVersionRange(
                minimum = NmsVersion(major, minor, minimumPatch),
                maximum = NmsVersion(major, minor, maximumPatch)
            )
    }
}