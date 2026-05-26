package com.github.prime

import org.bukkit.plugin.java.JavaPlugin

class PaperPlatform(
    private val plugin: JavaPlugin
) : Platform {
    override val type: PlatformType = PlatformType.PAPER

    override val console: Console = PaperConsole(plugin.logger)

    override val scheduler: PlatformScheduler =
        object : PlatformScheduler {
            override fun sync(task: () -> Unit) {
                plugin.server.scheduler.runTask(plugin, Runnable(task))
            }

            override fun async(task: () -> Unit) {
                plugin.server.scheduler.runTaskAsynchronously(plugin, Runnable(task))
            }
        }
}