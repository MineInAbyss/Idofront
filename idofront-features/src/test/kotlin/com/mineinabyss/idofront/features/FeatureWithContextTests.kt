package com.mineinabyss.idofront.features

import org.junit.jupiter.api.Test

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
}
