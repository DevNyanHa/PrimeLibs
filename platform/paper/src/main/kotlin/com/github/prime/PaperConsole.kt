package com.github.prime

import java.util.logging.Logger

class PaperConsole(
    private val logger: Logger
) : Console() {
    override fun info(text: LogText) {
        logger.info(text.formatted)
    }

    override fun warn(text: LogText) {
        logger.warning(text.formatted)
    }

    override fun error(text: LogText) {
        logger.severe(text.formatted)
    }
}