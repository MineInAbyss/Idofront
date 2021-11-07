package com.mineinabyss.idofront.font

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class NegativeSpaceTest {
    @Test
    fun of() {
        NegativeSpace.of(1) shouldBe NegativeSpace.MINUS_1
        NegativeSpace.of(1024) shouldBe NegativeSpace.MINUS_1024
        NegativeSpace.of(11) shouldBe "${NegativeSpace.MINUS_1}${NegativeSpace.MINUS_2}${NegativeSpace.MINUS_8}"
    }
}
