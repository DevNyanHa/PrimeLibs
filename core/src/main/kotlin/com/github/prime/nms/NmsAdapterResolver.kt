package com.github.prime.nms

import com.github.prime.Platform
import com.github.prime.PlatformType
import com.github.prime.nms.version.NmsVersion

object NmsAdapterResolver {
    fun resolve(
        platform: Platform,
        version: String,
        classLoader: ClassLoader
    ): NmsResolution? {
        if (platform.type == PlatformType.VELOCITY) return null

        return NmsRegistry(classLoader).resolve(NmsVersion.parse(version))
            ?: error("No NMS adapter is registered for $version.")
    }
}