package com.mineinabyss.idofront.config

import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream

class MergeConfigUpdatesTest {
    @Serializable
    data class MyDefaultConfig(val hello: String, val default: String = "world")

    @Test
    fun `merge updates`() {
        val output = ByteArrayOutputStream()
        config<MyDefaultConfig>("config") {
            mergeUpdates = true
            // set output to an output stream that writes to a string
            fromInputStream { ext -> "hello: world".takeIf { ext == "yml" }?.byteInputStream() }
            toOutputStream { ext -> output.takeIf { ext == "yml" } }
        }
        output.toString() shouldBe """
            hello: "world"
            default: "world"

            """.trimIndent()
    }
}
