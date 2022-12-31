package com.mineinabyss.idofront.config

import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Test

internal class ConfigBuilderTest {
    @Serializable
    data class MyConfig(val hello: String)

    @Test
    fun `create config`() {
        val myConfig = config<MyConfig>("test") {
            fromInputStream { ext -> "hello: world".takeIf { ext == "yml" }?.byteInputStream() }
        }
        val configData: MyConfig by myConfig
        configData shouldBe MyConfig(hello = "world")
    }
}
