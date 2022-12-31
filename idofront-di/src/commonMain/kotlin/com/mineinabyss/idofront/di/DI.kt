package com.mineinabyss.idofront.di

/**
 * Main class for registering and observing dependency injection modules.
 */
object DI : DIContext()

fun main() {
    DI.observe<String>()
}
