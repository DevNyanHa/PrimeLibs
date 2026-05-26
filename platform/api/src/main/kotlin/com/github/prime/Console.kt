package com.github.prime

enum class LogType {
    INFO,
    WARN,
    ERROR
}

abstract class Console {
    class LogText(
        val level: LogType,
        val text: String
    ) {
        val formatted: String = "| $text"
    }

    fun log(text: LogText) {
        when (text.level) {
            LogType.INFO -> info(text)
            LogType.WARN -> warn(text)
            LogType.ERROR -> error(text)
        }
    }

    protected abstract fun info(text: LogText)

    protected abstract fun warn(text: LogText)

    protected abstract fun error(text: LogText)
}