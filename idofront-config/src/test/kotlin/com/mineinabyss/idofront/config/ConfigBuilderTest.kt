package com.mineinabyss.idofront.config

import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.dsl.module

internal class ConfigBuilderTest : KoinComponent {
    @Serializable
    data class MyConfig(val hello: String)

    //@Test
    fun createConfig() {
        startKoin {
            modules(module {
                singleConfig(config<MyConfig>("test") {
                    fromInputStream { ext -> "hello: world".takeIf { ext == "yml" }?.byteInputStream() }
                })
            })
        }

        val config: MyConfig by inject()
        config shouldBe MyConfig(hello = "world")
    }
}
