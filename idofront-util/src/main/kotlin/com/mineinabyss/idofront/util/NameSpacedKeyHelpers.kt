package com.mineinabyss.idofront.util

import com.google.common.cache.CacheBuilder
import org.bukkit.NamespacedKey

// Bukkit uses a regex in the constructor to validate keys 💀
private val memoizedKeys = CacheBuilder.newBuilder().maximumSize(100).build<String, NamespacedKey>()

/** Converts this string to a [NamespacedKey] where the string must be formatted as `namespace:key` */
fun String.toMCKey(): NamespacedKey {
    memoizedKeys.getIfPresent(this)?.let { return it }
    val split = split(':')
    if (split.size != 2)
        error("Malformed key: $this, must only contain one : that splits namespace and key.")

    val (namespace, key) = split

    return NamespacedKey(namespace, key).also { memoizedKeys.put(this, it) }
}
