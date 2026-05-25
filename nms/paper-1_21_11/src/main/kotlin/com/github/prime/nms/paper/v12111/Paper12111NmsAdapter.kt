package com.github.prime.nms.paper.v12111

import com.github.prime.nms.NmsAdapter
import com.github.prime.nms.version.NmsVersionRange

/**
 * Paper 1.21.x 라인의 NMS 기능을 제공하는 어댑터입니다.
 */
class Paper12111NmsAdapter : NmsAdapter {
    override val id: String = "paper-1_21_11"

    override val nmsVersionRange: NmsVersionRange =
        NmsVersionRange.between("1.21.0", "1.21.11")
}