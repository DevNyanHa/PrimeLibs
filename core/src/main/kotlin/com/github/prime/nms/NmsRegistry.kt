package com.github.prime.nms

import com.github.prime.nms.version.NmsVersion
import java.util.ServiceLoader

class NmsRegistry(
    private val classLoader: ClassLoader = NmsRegistry::class.java.classLoader
) {
    private val factories: List<NmsAdapterFactory> by lazy {
        ServiceLoader
            .load(NmsAdapterFactory::class.java, classLoader)
            .toList()
            .sortedByDescending { it.supportedVersions.minimum }
    }

    fun resolve(version: NmsVersion): NmsResolution? {
        val match =
            factories.firstOrNull { it.supportedVersions.contains(version) }
                ?: return null

        return NmsResolution(
            requestedVersion = version,
            factoryId = match.id,
            adapter = match.create()
        )
    }

    fun describeFactories(): String = factories.joinToString { "${it.id} (${it.supportedVersions})" }
}