package com.github.prime.nms

import com.github.prime.nms.version.NmsVersion

data class NmsResolution(
    val requestedVersion: NmsVersion,
    val adapter: NmsAdapter
)