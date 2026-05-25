package com.github.prime

import com.velocitypowered.api.proxy.ProxyServer

class VelocityPlatform(
    private val plugin: Any,
    private val server: ProxyServer
) : Platform {
    override val type: PlatformType = PlatformType.VELOCITY

    override val scheduler: PlatformScheduler =
        object : PlatformScheduler {
            override fun sync(task: () -> Unit) {
                server.scheduler
                    .buildTask(plugin, Runnable(task))
                    .schedule()
            }

            override fun async(task: () -> Unit) {
                server.scheduler
                    .buildTask(plugin, Runnable(task))
                    .schedule()
            }
        }
}