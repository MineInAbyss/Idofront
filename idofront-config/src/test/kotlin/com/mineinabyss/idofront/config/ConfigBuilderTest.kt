package com.mineinabyss.idofront.config

import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Test
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.dsl.module

internal class ConfigBuilderTest : KoinComponent {
    @Serializable
    data class MyConfig(val hello: String)

    @Test
    fun createConfig() {
        val myConfig = config<MyConfig>("test") {
            default(MyConfig())
            fromInputStream { ext -> "hello: world".takeIf { ext == "yml" }?.byteInputStream() }
        }
        val stringConfig = config<String>("") {
            fromInputStream { ext -> "test".takeIf { ext == "yml" }?.byteInputStream() }
        }

        startKoin {
            modules(module {
                singleConfig(myConfig)
                singleConfig(stringConfig)
            })
        }

        val config: IdofrontConfig<MyConfig> by injectConfig()
        val otherConfig: IdofrontConfig<String> by injectConfig()
        val configData: MyConfig by inject()

        config shouldBe myConfig
        otherConfig shouldBe stringConfig
        configData shouldBe MyConfig(hello = "world")
    }
}
