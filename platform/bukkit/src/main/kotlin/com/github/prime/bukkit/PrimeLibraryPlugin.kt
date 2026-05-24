package com.github.prime.bukkit

import com.github.prime.PaperPlatform
import com.github.prime.PrimeLibsBootstrap
import com.github.prime.PrimeLibsRuntime
import org.bukkit.plugin.java.JavaPlugin

class PrimeLibraryPlugin : JavaPlugin() {
    private lateinit var runtime: PrimeLibsRuntime

    override fun onEnable() {
        val minecraftVersion = server.minecraftVersion

        runtime =
            PrimeLibsBootstrap.bootstrap(
                platform = PaperPlatform(this),
                minecraftVersion = minecraftVersion,
                classLoader = javaClass.classLoader
            )

        logStartupBanner(minecraftVersion)
        logger.info(runtime.startupSummary())
        logger.info("Capabilities: ${runtime.nms.adapter.describeCapabilities()}")
    }

    private fun logStartupBanner(minecraftVersion: String) {
        listOf(
            " ",
            "  ____       _                _     _ _",
            " |  _ \\ _ __(_)_ __ ___   ___| |   (_) |__  ___",
            " | |_) | '__| | '_ ` _ \\ / _ \\ |   | | '_ \\/ __|",
            " |  __/| |  | | | | | | |  __/ |___| | |_) \\__ \\",
            " |_|   |_|  |_|_| |_| |_|\\___|_____|_|_.__/|___/",
            " Platform : ${runtime.platform.type}",
            " Version  : $minecraftVersion",
            " Adapter  : ${runtime.nms.adapter.displayName}",
            " "
        ).forEach(logger::info)
    }
}