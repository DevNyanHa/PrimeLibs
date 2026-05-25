package com.github.prime.nms

import com.github.prime.nms.version.NmsVersion
import java.util.ServiceLoader

class NmsRegistry(
    private val classLoader: ClassLoader = NmsRegistry::class.java.classLoader
) {
    private val nmsAdapters: List<NmsAdapter> by lazy {
        ServiceLoader
            .load(NmsAdapter::class.java, classLoader)
            .toList()
            .distinctBy(NmsAdapter::id)
            .sortedByDescending { it.nmsVersionRange.minimum }
    }

    fun resolve(version: NmsVersion): NmsResolution? {
        val match =
            nmsAdapters.firstOrNull { it.nmsVersionRange.contains(version) }
                ?: return null

        return NmsResolution(
            requestedVersion = version,
            adapter = match
        )
    }
}