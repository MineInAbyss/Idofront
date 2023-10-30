package com.mineinabyss.idofront.font

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class SpaceTest {
    @Test
    fun of() {
        Space.of(-1) shouldBe "${Space.MINUS_1}"
        Space.of(-1024) shouldBe "${Space.MINUS_1024}"
        Space.of(-11) shouldBe "${Space.MINUS_8}${Space.MINUS_2}${Space.MINUS_1}"
        Space.of(11) shouldBe "${Space.PLUS_8}${Space.PLUS_2}${Space.PLUS_1}"
        Space.of(0) shouldBe ""
    }
}
