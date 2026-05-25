package com.github.prime.nms.paper.v2612

import com.github.prime.nms.NmsAdapter
import com.github.prime.nms.version.NmsVersionRange

/**
 * Paper 26.1.x 라인의 NMS 기능을 제공하는 어댑터입니다.
 */
class Paper2612NmsAdapter : NmsAdapter {
    override val id: String = "paper-26_1_2"

    override val nmsVersionRange: NmsVersionRange =
        NmsVersionRange.featureLine(26, 1)
}