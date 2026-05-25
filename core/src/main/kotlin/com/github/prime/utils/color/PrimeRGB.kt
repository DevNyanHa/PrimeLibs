package com.github.prime.utils.color

data class PrimeRGB(
    val r: Int,
    val g: Int,
    val b: Int
) {
    init {
        require(
            r in 0..255 &&
            g in 0..255 &&
            b in 0..255
        ) { "Must between 0 and 255" }
    }
}
