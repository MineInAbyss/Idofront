package com.mineinabyss.idofront.di

import io.kotest.matchers.shouldBe
import kotlin.test.Test


class DefaultingModuleObserverTests {
    @Test
    fun `should return default calculation when not injected`() {
        val observer = DI.observe<Int>().default { 1 }
        observer.get() shouldBe 1
    }
    @Test
    fun `should override default when injected`() {
        val observer = DI.observe<Int>().default { 1 }

        observer.get() shouldBe 1
        DI.add(2)
        observer.get() shouldBe 2
    }
}
