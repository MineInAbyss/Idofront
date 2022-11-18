package com.mineinabyss.idofront.config

import java.io.InputStream
import java.nio.file.Path

data class InputWithExt(val input: InputStream, val ext: String)

interface ConfigGetter<T> {
    fun get(): Result<T>
}

interface InputGetter {
    fun read(): Result<InputWithExt>
}

interface Savable {

}
class FileInputGetter(val pathNoExt: Path, val formats: Formats) : InputGetter, Savable {
    override fun read(): Result<InputWithExt> = runCatching {
        formats.keys.firstNotNullOf { ext ->
            this::class.java.getResourceAsStream("/$pathNoExt.$ext")?.let { stream ->
                InputWithExt(stream, ext)
            }
        }
    }
}

class DefaultConfigGetter<T>(val input: ConfigGetter<T>, val default: ConfigGetter<T>) : ConfigGetter<T> {
    override fun get(): Result<T> {
        return input.get().recoverCatching {
            default.get().getOrThrow()
        }
    }
}
