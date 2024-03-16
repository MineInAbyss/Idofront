package com.mineinabyss.idofront.di

import io.kotest.matchers.shouldBe
import kotlin.test.Test


class ScopedDITests {
    object A
    object B

    @Test
    fun `should restrict injections when scoped`() {
        DI.scoped("1").add(A)
        DI.scoped("2").add(B)

        DI.getOrNull<A>() shouldBe null
        DI.getOrNull<B>() shouldBe null

        DI.scoped("1").getOrNull<A>() shouldBe A
        DI.scoped("1").getOrNull<B>() shouldBe null
        DI.scoped("2").getOrNull<A>() shouldBe null
        DI.scoped("2").getOrNull<B>() shouldBe B
    }

    @Test
    fun `should correctly observe on scope`() {
        DI.scoped("1").add(A)
        val observer =DI.scoped("1").observe<A>()

        observer.get() shouldBe A
    }
}
