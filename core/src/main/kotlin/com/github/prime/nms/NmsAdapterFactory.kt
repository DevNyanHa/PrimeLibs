package com.github.prime.nms

import com.github.prime.nms.version.NmsVersionRange

interface NmsAdapterFactory {
    val id: String
    val supportedVersions: NmsVersionRange

    fun create(): NmsAdapter
}