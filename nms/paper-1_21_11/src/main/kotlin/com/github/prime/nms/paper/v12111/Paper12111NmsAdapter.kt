package com.github.prime.nms.paper.v12111

import com.github.prime.nms.NmsAdapter
import com.github.prime.nms.NmsAdapterFactory
import com.github.prime.nms.version.NmsVersionRange

class Paper12111NmsAdapter : NmsAdapter {
    override val id: String = "paper-1_21_11"
    override val displayName: String = "Paper 1.21.x adapter"

    override fun describeCapabilities(): String =
        "Prepared for 1.21.x-specific internals and legacy compatibility hooks."
}

class Paper12111NmsAdapterFactory : NmsAdapterFactory {
    override val id: String = "paper-1_21_11"
    override val supportedVersions: NmsVersionRange =
        NmsVersionRange.between("1.21.0", "1.21.11")

    override fun create(): NmsAdapter = Paper12111NmsAdapter()
}