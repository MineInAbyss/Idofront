package com.mineinabyss.idofront.features

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class FeatureWithContextTests {
    class Context(val message: String = "hello")

    @Test
    fun `should be able to inject context`() {
        // arrange
        val feature = feature("test") {
            scopedModule {
                scoped { Context("custom") }
            }
        }

        // act
//        feature.createAndInjectContext()

        // assert
//        DI.get<Context>().shouldNotBeNull()
    }

    @Test
    fun `koin loading assumptions`() {
        koinApplication {
            modules(module {
                single { "1" }
            })
            modules(module {
                single { "2" }
            })
        }.koin.get<String>().shouldBe("2")
    }
}
