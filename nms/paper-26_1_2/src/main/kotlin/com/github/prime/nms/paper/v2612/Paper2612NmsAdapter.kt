package com.github.prime.nms.paper.v2612

import com.github.prime.nms.NmsAdapter
import com.github.prime.nms.NmsAdapterFactory
import com.github.prime.nms.version.NmsVersionRange

class Paper2612NmsAdapter : NmsAdapter {
    override val id: String = "paper-26_1_2"
    override val displayName: String = "Paper 26.1.x adapter"

    override fun describeCapabilities(): String =
        "Prepared for 26.1.x internals, unobfuscated runtime, and modern Paper behavior."
}

class Paper2612NmsAdapterFactory : NmsAdapterFactory {
    override val id: String = "paper-26_1_2"
    override val supportedVersions: NmsVersionRange =
        NmsVersionRange.featureLine(26, 1)

    override fun create(): NmsAdapter = Paper2612NmsAdapter()
}