package com.github.prime

enum class PlatformType {
    PAPER,
    FOLIA,
    VELOCITY
}

interface PlatformScheduler {
    fun sync(task: () -> Unit)

    fun async(task: () -> Unit)
}

interface Platform {
    val type: PlatformType

    val console: Console

    val scheduler: PlatformScheduler
}