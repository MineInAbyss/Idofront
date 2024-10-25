package com.mineinabyss.idofront.commands.brigadier

import com.mineinabyss.idofront.commands.brigadier.context.IdoCommandContext
import com.mojang.brigadier.arguments.ArgumentType


inline fun <reified A: Any> IdoCommand.executes(
    a: ArgumentType<A>,
    crossinline run: IdoCommandContext.(A) -> Unit
) {
    executesDefaulting(a) { (a) -> run(arg<A>(a)) }
}

inline fun <reified A: Any, reified B: Any> IdoCommand.executes(
    a: ArgumentType<A>,
    b: ArgumentType<B>,
    crossinline run: IdoCommandContext.(A, B) -> Unit
) {
    executesDefaulting(a, b) { (a, b) -> run(arg<A>(a), arg<B>(b)) }
}

inline fun <reified A: Any, reified B: Any, reified C: Any> IdoCommand.executes(
    a: ArgumentType<A>,
    b: ArgumentType<B>,
    c: ArgumentType<C>,
    crossinline run: IdoCommandContext.(A, B, C) -> Unit
) {
    executesDefaulting(a, b, c) { (a, b, c) -> run(arg<A>(a), arg<B>(b), arg<C>(c)) }
}

inline fun <reified A: Any, reified B: Any, reified C: Any, reified D: Any> IdoCommand.executes(
    a: ArgumentType<A>,
    b: ArgumentType<B>,
    c: ArgumentType<C>,
    d: ArgumentType<D>,
    crossinline run: IdoCommandContext.(A, B, C, D) -> Unit
) {
    executesDefaulting(a, b, c, d) { (a, b, c, d) -> run(arg<A>(a), arg<B>(b), arg<C>(c), arg<D>(d)) }
}

inline fun <reified A: Any, reified B: Any, reified C: Any, reified D: Any, reified E: Any> IdoCommand.executes(
    a: ArgumentType<A>,
    b: ArgumentType<B>,
    c: ArgumentType<C>,
    d: ArgumentType<D>,
    e: ArgumentType<E>,
    crossinline run: IdoCommandContext.(A, B, C, D, E) -> Unit
) {
    executesDefaulting(a, b, c, d, e) { (a, b, c, d, e) -> run(arg<A>(a), arg<B>(b), arg<C>(c), arg<D>(d), arg<E>(e)) }
}

inline fun <reified A: Any, reified B: Any, reified C: Any, reified D: Any, reified E: Any, reified F: Any> IdoCommand.executes(
    a: ArgumentType<A>,
    b: ArgumentType<B>,
    c: ArgumentType<C>,
    d: ArgumentType<D>,
    e: ArgumentType<E>,
    f: ArgumentType<F>,
    crossinline run: IdoCommandContext.(A, B, C, D, E, F) -> Unit
) {
    executesDefaulting(a, b, c, d, e, f) { run(arg<A>(it[0]), arg<B>(it[1]), arg<C>(it[2]), arg<D>(it[3]), arg<E>(it[4]), arg<F>(it[5])) }
}
