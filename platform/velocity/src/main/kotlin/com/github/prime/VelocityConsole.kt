package com.github.prime

import org.slf4j.Logger

class VelocityConsole(
    private val logger: Logger
) : Console() {
    override fun info(text: LogText) {
        logger.info(text.formatted)
    }

    override fun warn(text: LogText) {
        logger.warn(text.formatted)
    }

    override fun error(text: LogText) {
        logger.error(text.formatted)
    }
}