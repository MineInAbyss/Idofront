package com.mineinabyss

import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.DependencyHandlerScope
import java.net.URI

fun RepositoryHandler.mineInAbyss() = maven {
    name = "MineInAbyss"
    url = URI("https://repo.mineinabyss.com/releases")
}

fun DependencyHandlerScope.kotlinSpice(version: String) =
    "compileOnly"(platform("com.mineinabyss:kotlinspice:$version"))


