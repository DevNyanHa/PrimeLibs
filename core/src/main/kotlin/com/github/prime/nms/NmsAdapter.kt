package com.github.prime.nms

interface NmsAdapter {
    val id: String
    val displayName: String

    fun describeCapabilities(): String
}