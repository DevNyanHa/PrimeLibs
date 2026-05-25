package com.github.prime

import com.github.prime.nms.NmsAdapterResolver
import com.github.prime.nms.NmsResolution

/**
 * PrimeLibs 초기화 결과를 담는 런타임 컨텍스트입니다.
 *
 * @property platform 초기화된 플랫폼
 * @property version 플랫폼에서 보고한 서버 또는 프록시 버전
 * @property nms 선택된 NMS 해석 결과. NMS가 없는 플랫폼에서는 null입니다.
 */
data class PrimeLibsRuntime(
    val platform: Platform,
    val version: String,
    val nms: NmsResolution?
)

object PrimeBootstrap {
    /**
     * 플랫폼과 버전을 기반으로 PrimeLibs 런타임을 초기화합니다.
     *
     * @param platform 현재 실행 플랫폼
     * @param version 플랫폼에서 보고한 서버 또는 프록시 버전 문자열
     * @param classLoader NMS 어댑터를 탐색할 클래스 로더
     * @return 초기화된 [PrimeLibsRuntime]
     * @throws IllegalStateException NMS가 필요한 플랫폼에서 사용할 수 있는 어댑터가 없는 경우 발생합니다.
     */
    fun bootstrap(
        platform: Platform,
        version: String,
        classLoader: ClassLoader = PrimeBootstrap::class.java.classLoader
    ): PrimeLibsRuntime {
        PlatformProvider.register(platform)

        return PrimeLibsRuntime(
            platform = platform,
            version = version,
            nms = NmsAdapterResolver.resolve(platform, version, classLoader)
        )
    }
}