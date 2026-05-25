package com.github.prime.nms

import com.github.prime.nms.version.NmsVersionRange

interface NmsAdapter {
    val id: String

    val nmsVersionRange: NmsVersionRange
}