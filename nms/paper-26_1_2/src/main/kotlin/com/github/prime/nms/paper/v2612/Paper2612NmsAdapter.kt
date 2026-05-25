package com.github.prime.nms.paper.v2612

import com.github.prime.nms.NmsAdapter
import com.github.prime.nms.version.NmsVersionRange

class Paper2612NmsAdapter : NmsAdapter {
    override val id: String = "paper-26_1_2"

    override val nmsVersionRange: NmsVersionRange =
        NmsVersionRange.featureLine(26, 1)
}