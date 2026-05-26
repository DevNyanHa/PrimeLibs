package com.github.prime

import com.github.prime.nms.NmsAdapter

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

        val nmsAdapter = NmsAdapter.resolve(platform, version, classLoader)

        platform.console.log(
            Console.LogText(
                LogType.INFO,
                "Platform: ${platform.type} Version: $version Adapter: ${nmsAdapter?.id ?: "none"}"
            )
        )

        return PrimeRuntime(
            platformType = platform.type,
            version = version,
            nmsAdapter = nmsAdapter
        )
    }
}