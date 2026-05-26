package com.github.prime

import com.github.prime.VelocityConsole
import com.velocitypowered.api.proxy.ProxyServer
import org.slf4j.Logger as Slf4jLogger

class VelocityPlatform(
    private val plugin: Any,
    private val server: ProxyServer,
    nativeLogger: Slf4jLogger
) : Platform {
    override val type: PlatformType = PlatformType.VELOCITY

    override val console: Console = VelocityConsole(nativeLogger)

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