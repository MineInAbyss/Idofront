package com.mineinabyss.idofront.commands.brigadier

import com.mojang.brigadier.arguments.ArgumentType


inline fun <reified A: Any> IdoCommand.playerExecutes(
    a: ArgumentType<A>,
    crossinline run: IdoCommandContext.(A) -> Unit
) {
    val rA = registerArgument(a, "a")
    playerExecutes { run(rA()) }
}

inline fun <reified A: Any, reified B: Any> IdoCommand.playerExecutes(
    a: ArgumentType<A>,
    b: ArgumentType<B>,
    crossinline run: IdoCommandContext.(A, B) -> Unit
) {
    val rA = registerArgument(a, "a")
    val rB = registerArgument(b, "b")
    playerExecutes { run(rA(), rB()) }
}

inline fun <reified A: Any, reified B: Any, reified C: Any> IdoCommand.playerExecutes(
    a: ArgumentType<A>,
    b: ArgumentType<B>,
    c: ArgumentType<C>,
    crossinline run: IdoCommandContext.(A, B, C) -> Unit
) {
    val rA = registerArgument(a, "a")
    val rB = registerArgument(b, "b")
    val rC = registerArgument(c, "c")
    playerExecutes { run(rA(), rB(), rC()) }
}

inline fun <reified A: Any, reified B: Any, reified C: Any, reified D: Any> IdoCommand.playerExecutes(
    a: ArgumentType<A>,
    b: ArgumentType<B>,
    c: ArgumentType<C>,
    d: ArgumentType<D>,
    crossinline run: IdoCommandContext.(A, B, C, D) -> Unit
) {
    val rA = registerArgument(a, "a")
    val rB = registerArgument(b, "b")
    val rC = registerArgument(c, "c")
    val rD = registerArgument(d, "d")
    playerExecutes { run(rA(), rB(), rC(), rD()) }
}

inline fun <reified A: Any, reified B: Any, reified C: Any, reified D: Any, reified E: Any> IdoCommand.playerExecutes(
    a: ArgumentType<A>,
    b: ArgumentType<B>,
    c: ArgumentType<C>,
    d: ArgumentType<D>,
    e: ArgumentType<E>,
    crossinline run: IdoCommandContext.(A, B, C, D, E) -> Unit
) {
    val rA = registerArgument(a, "a")
    val rB = registerArgument(b, "b")
    val rC = registerArgument(c, "c")
    val rD = registerArgument(d, "d")
    val rE = registerArgument(e, "e")
    playerExecutes { run(rA(), rB(), rC(), rD(), rE()) }
}
