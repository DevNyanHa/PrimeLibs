package com.github.prime

object PlatformProvider {
    lateinit var platform: Platform
        private set

    fun register(platform: Platform) {
        this.platform = platform
    }
}