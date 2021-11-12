package com.mineinabyss.idofront.util

import org.bukkit.NamespacedKey

/** Converts this string to a [NamespacedKey] where the string must be formatted as `namespace:key` */
fun String.toMCKey(): NamespacedKey {
    val split = split(':')
    if (split.size != 2)
        error("Malformed key: $this, must only contain one : that splits namespace and key.")

    val (namespace, key) = split

    @Suppress("DEPRECATION") // deprecated just to discourage using instantiating without plugin reference
    return NamespacedKey(namespace, key)
}
