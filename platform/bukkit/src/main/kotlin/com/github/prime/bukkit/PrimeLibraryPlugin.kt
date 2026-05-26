package com.github.prime.bukkit

import com.github.prime.PaperPlatform
import com.github.prime.PrimeBootstrap
import com.github.prime.PrimeRuntime
import org.bukkit.plugin.java.JavaPlugin

class PrimeLibraryPlugin : JavaPlugin() {
    private lateinit var runtime: PrimeRuntime

    override fun onEnable() {
        val minecraftVersion = server.minecraftVersion

        runtime =
            PrimeBootstrap.bootstrap(
                platform = PaperPlatform(this),
                version = minecraftVersion,
                classLoader = javaClass.classLoader
            )
    }
}