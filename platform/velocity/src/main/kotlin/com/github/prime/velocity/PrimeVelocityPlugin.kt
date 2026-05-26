package com.github.prime.velocity

import com.github.prime.PrimeBootstrap
import com.github.prime.PrimeRuntime
import com.github.prime.VelocityPlatform
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import org.slf4j.Logger

@Plugin(
    id = "primelibs",
    name = "PrimeLibs",
    description = "Multi-module, multi-version support library plugin scaffold for Velocity.",
    authors = ["prime"]
)
class PrimeVelocityPlugin {
    private val server: ProxyServer
    private val logger: Logger
    private lateinit var runtime: PrimeRuntime

    @Inject
    constructor(
        server: ProxyServer,
        logger: Logger
    ) {
        this.server = server
        this.logger = logger
    }

    @Subscribe
    fun onProxyInitialize(event: ProxyInitializeEvent) {
        runtime =
            PrimeBootstrap.bootstrap(
                platform = VelocityPlatform(this, server, logger),
                version = server.version.version,
                classLoader = javaClass.classLoader
            )
    }
}