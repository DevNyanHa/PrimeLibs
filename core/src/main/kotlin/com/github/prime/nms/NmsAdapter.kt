package com.github.prime.nms

import com.github.prime.Platform
import com.github.prime.PlatformType
import com.github.prime.nms.version.NmsVersion
import com.github.prime.nms.version.NmsVersionRange

/**
 * @property id 어댑터를 식별하는 고유 id
 * @property nmsVersionRange 어댑터가 지원하는 NMS 버전 범위
 */
interface NmsAdapter {
    val id: String

    val nmsVersionRange: NmsVersionRange

    companion object {
        /**
         * 플랫폼과 버전에 맞는 [NmsAdapter]를 탐색합니다.
         *
         * @param platform 현재 실행 플랫폼
         * @param version 플랫폼에서 보고한 서버 버전 문자열
         * @param classLoader 어댑터 구현체를 탐색할 클래스 로더
         * @return 선택된 [NmsAdapter]. NMS가 없는 플랫폼에서는 null입니다.
         * @throws IllegalStateException NMS가 필요한 플랫폼에서 사용할 수 있는 어댑터가 없는 경우 발생합니다.
         */
        fun resolve(
            platform: Platform,
            version: String,
            classLoader: ClassLoader
        ): NmsAdapter? {
            if (platform.type == PlatformType.VELOCITY) return null

            return NmsRegistry(classLoader).resolve(NmsVersion.parse(version))
                ?: error("No NMS adapter is registered for $version.")
        }
    }
}