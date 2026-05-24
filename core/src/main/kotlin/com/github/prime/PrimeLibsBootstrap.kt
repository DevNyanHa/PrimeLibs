package com.github.prime

import com.github.prime.nms.NmsRegistry
import com.github.prime.nms.NmsResolution
import com.github.prime.nms.version.NmsVersion

data class PrimeLibsRuntime(
    val platform: Platform,
    val nms: NmsResolution
) {
    fun startupSummary(): String =
        "PrimeLibs initialized on ${platform.type} " +
            "for Minecraft ${nms.requestedVersion} " +
            "using adapter ${nms.adapter.displayName}"
}

object PrimeLibsBootstrap {
    fun bootstrap(
        platform: Platform,
        minecraftVersion: String,
        classLoader: ClassLoader = PrimeLibsBootstrap::class.java.classLoader
    ): PrimeLibsRuntime {
        PlatformProvider.register(platform)

        val registry = NmsRegistry(classLoader)
        val requestedVersion = NmsVersion.parse(minecraftVersion)
        val resolution =
            registry.resolve(requestedVersion)
                ?: error(
                    "No NMS adapter is registered for $minecraftVersion. " +
                        "Registered adapters: ${registry.describeFactories()}"
                )

        return PrimeLibsRuntime(
            platform = platform,
            nms = resolution
        )
    }
}