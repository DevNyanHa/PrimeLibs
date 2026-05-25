package com.github.prime.nms.paper.v12111

import com.github.prime.nms.NmsAdapter
import com.github.prime.nms.version.NmsVersionRange

class Paper12111NmsAdapter : NmsAdapter {
    override val id: String = "paper-1_21_11"

    override val nmsVersionRange: NmsVersionRange =
        NmsVersionRange.between("1.21.0", "1.21.11")
}