package com.github.prime

import com.github.prime.nms.NmsAdapter

//TODO: 얘도 손봐야함
data class PrimeRuntime(
    val platformType: PlatformType,
    val version: String,
    val nmsAdapter: NmsAdapter?
)

object PrimeBootstrap {
    fun bootstrap(
        platform: Platform,
        version: String,
        classLoader: ClassLoader = PrimeBootstrap::class.java.classLoader
    ): PrimeRuntime {
        PlatformProvider.register(platform)

        return PrimeRuntime(
            platformType = platform.type,
            version = version,
            nmsAdapter = NmsAdapter.resolve(platform, version, classLoader)
        )
    }
}