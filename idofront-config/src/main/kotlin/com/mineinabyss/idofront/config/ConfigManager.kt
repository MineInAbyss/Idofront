package com.mineinabyss.idofront.config

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.KSerializer
import kotlinx.serialization.StringFormat
import kotlinx.serialization.modules.SerializersModule
import java.nio.file.Path

inline fun <reified T> config(block: ConfigBuilder<T>.() -> Unit): ConfigManager<T> {
    return TODO() //ConfigBuilder(serializer<T>(), format)
}

data class ConfigBuilder<T>(
    var serializer: KSerializer<T>,
    var format: StringFormat
) {
    var serializersModule: SerializersModule? = null
}

class ConfigManager<T> {
    private val defaultPath: Path? = null

    fun single(path: Path) {

    }

    fun directory(path: Path): List<ConfigEntry<T>> {
        TODO()
    }
//    fun read(path: Path = defaultPath ?: error("No default path specified")): T = TODO()
//
//    fun write(value: T, path: Path = defaultPath ?: error("No default path specified")) {
//
//    }

    fun readFromDirectory(path: Path): List<ConfigEntry<T>> = TODO()
}

data class ConfigEntry<T>(val path: Path, val data: T)

data class Test(
    val a: String
)
fun main() {
    config<Test> {
        format = Yaml.default
    }.single(Path.of("test.yml"))
}