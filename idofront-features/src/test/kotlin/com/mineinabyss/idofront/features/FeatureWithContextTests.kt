package com.mineinabyss.idofront.features

import com.mineinabyss.idofront.di.DI
import io.kotest.matchers.nulls.shouldNotBeNull
import org.junit.jupiter.api.Test

class FeatureWithContextTests {
    class Context(val message: String = "hello")
    @Test
    fun `should be able to inject context`() {
        // arrange
        val feature = object: FeatureWithContext<Context>(::Context) {
        }

        // act
        feature.createAndInjectContext()

        // assert
        DI.get<Context>().shouldNotBeNull()
    }
}
