package com.mineinabyss.idofront.util

fun String.removeSpaces() = replace(" ", "")
fun String.remove(remove: String) = replace(remove, "")
fun String.appendIfMissing(suffix: String) = if (endsWith(suffix)) this else (this + suffix)
fun String.prependIfMissing(prefix: String) = if (startsWith(prefix)) this else (prefix + this)
fun String.substringBetween(after: String, before: String) = this.substringAfter(after).substringBefore(before)
fun String.substringBetweenLast(after: String, before: String) = this.substringAfter(after).substringBeforeLast(before)
fun <T> String.ifNotEmpty(block: (String) -> T): T? = if (this.isNotEmpty()) block(this) else null