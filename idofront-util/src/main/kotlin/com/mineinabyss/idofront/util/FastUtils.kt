package com.mineinabyss.idofront.util

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import java.util.*

fun <T> Sequence<T>.toFastList(): ObjectArrayList<T> {
    val it = iterator()
    if (!it.hasNext())
        return ObjectArrayList()
    val element = it.next()
    if (!it.hasNext())
        return ObjectArrayList.of(element)
    val dst = ObjectArrayList<T>()
    dst.add(element)
    while (it.hasNext()) dst.add(it.next())
    return dst
}

fun <T> Sequence<T>.toFastSet(): ObjectLinkedOpenHashSet<T> {
    val it = iterator()
    if (!it.hasNext())
        return ObjectLinkedOpenHashSet()
    val element = it.next()
    if (!it.hasNext())
        return ObjectLinkedOpenHashSet.of(element)
    val dst = ObjectLinkedOpenHashSet<T>()
    dst.add(element)
    while (it.hasNext()) dst.add(it.next())
    return dst
}

inline fun <T, R> Iterable<T>.flatMapFast(transform: (T) -> Iterable<R>): ObjectArrayList<R> {
    return flatMapTo(ObjectArrayList<R>(), transform)
}

inline fun <T, R> Iterable<T>.flatMapFastNotNull(transform: (T) -> Iterable<R?>): ObjectArrayList<R> {
    val result = ObjectArrayList<R>()
    for (element in this) {
        for (item in transform(element)) {
            if (item != null) result.add(item)
        }
    }
    return result
}

inline fun <T, R> Iterable<T>.flatMapSetFast(transform: (T) -> Iterable<R>): ObjectLinkedOpenHashSet<R> {
    return flatMapTo(ObjectLinkedOpenHashSet<R>(), transform)
}

inline fun <T, R> Iterable<T>.mapFast(transform: (T) -> R): ObjectArrayList<R> {
    return mapTo(ObjectArrayList<R>((this as? Collection)?.size ?: 10), transform)
}

inline fun <T, R : Any> Iterable<T>.mapNotNullFast(transform: (T) -> R?): ObjectArrayList<R> {
    return mapNotNullTo(ObjectArrayList<R>(), transform)
}

inline fun <T, R> Iterable<T>.mapFastSet(transform: (T) -> R): ObjectOpenHashSet<R> {
    return mapTo(ObjectOpenHashSet<R>((this as? Collection)?.size ?: 10), transform)
}

inline fun <T, reified R> Iterable<T>.toTypedArrayIndexed(transform: (Int, T) -> R): Array<R> {
    val result = arrayOfNulls<R>(this.count())
    var index = 0
    for (item in this) result[index] = transform(index, item).also { index++ }
    return result as Array<R>
}

inline fun <T, reified R> Iterable<T>.toTypedArray(transform: (T) -> R): Array<R> {
    val result = arrayOfNulls<R>(this.count())
    var index = 0
    for (item in this) result[index++] = transform(item)
    return result as Array<R>
}

inline fun <T, reified R> Array<T>.toTypedArray(transform: (T) -> R): Array<R> {
    val result = arrayOfNulls<R>(this.count())
    var index = 0
    for (item in this) result[index++] = transform(item)
    return result as Array<R>
}

inline fun <T, reified R : Any> Iterable<T>.toTypedArrayNotNull(transform: (T) -> R?): Array<R> {
    var result = arrayOfNulls<R>(this.count())
    var index = 0

    for (item in this) {
        transform(item)?.let {
            if (index >= result.size) {
                result = result.copyOf(maxOf(result.size * 2, this.count()))
            }
            result[index++] = it
        }
    }

    // Trim to exact size
    return result.copyOf(index) as Array<R>
}


inline fun <T, K, V> Iterable<T>.associateFast(transform: (T) -> Pair<K, V>): Object2ObjectOpenHashMap<K, V> {
    val capacity = mapCapacity((this as? Collection)?.size ?: 10).coerceAtLeast(16)
    return associateTo(Object2ObjectOpenHashMap<K, V>(capacity), transform)
}

inline fun <T, K, V> Array<T>.associateFast(transform: (T) -> Pair<K, V>): Object2ObjectOpenHashMap<K, V> {
    val capacity = mapCapacity(size).coerceAtLeast(16)
    return associateTo(Object2ObjectOpenHashMap<K, V>(capacity), transform)
}

inline fun <T, K> Iterable<T>.associateFastBy(keySelector: (T) -> K): Object2ObjectOpenHashMap<K, T> {
    val result = Object2ObjectOpenHashMap<K, T>(mapCapacity((this as? Collection)?.size ?: 10).coerceAtLeast(16))
    return associateByTo(result, keySelector)
}

inline fun <K, V> Iterable<K>.associateFastWith(valueSelector: (K) -> V): Object2ObjectOpenHashMap<K, V> {
    val result = Object2ObjectOpenHashMap<K, V>(mapCapacity((this as? Collection)?.size ?: 10).coerceAtLeast(16))
    return associateWithTo(result, valueSelector)
}

inline fun <T, K, V> Iterable<T>.associateFastLinked(pairSelector: (T) -> Pair<K, V>): Object2ObjectLinkedOpenHashMap<K, V> {
    val result = Object2ObjectLinkedOpenHashMap<K, V>(mapCapacity((this as? Collection)?.size ?: 10).coerceAtLeast(16))
    return associateTo(result, pairSelector)
}

inline fun <K, V> Iterable<K>.associateFastLinkedWith(valueSelector: (K) -> V): Object2ObjectLinkedOpenHashMap<K, V> {
    val result = Object2ObjectLinkedOpenHashMap<K, V>(mapCapacity((this as? Collection)?.size ?: 10).coerceAtLeast(16))
    return associateWithTo(result, valueSelector)
}

inline fun <T, K, V> Iterable<T>.associateFastNotNull(
    pairSelector: (T) -> Pair<K?, V?>?
): Object2ObjectOpenHashMap<K, V> {
    val result = Object2ObjectOpenHashMap<K, V>(mapCapacity((this as? Collection)?.size ?: 10).coerceAtLeast(16))
    for (element in this) {
        val (key, value) = pairSelector(element)?.let { (it.first ?: continue) to (it.second ?: continue) } ?: continue
        result[key] = value
    }
    return result
}

inline fun <T, K, V> Iterable<T>.associateFastLinkedNotNull(
    pairSelector: (T) -> Pair<K?, V?>?
): Object2ObjectLinkedOpenHashMap<K, V> {
    val result = Object2ObjectLinkedOpenHashMap<K, V>(mapCapacity((this as? Collection)?.size ?: 10).coerceAtLeast(16))
    for (element in this) {
        val (key, value) = pairSelector(element)?.let { (it.first ?: continue) to (it.second ?: continue) } ?: continue
        result[key] = value
    }
    return result
}

inline fun <T, K, V> Iterable<T>.associateFastByNotNull(
    keySelector: (T) -> K?,
    valueSelector: (T) -> V?
): Object2ObjectOpenHashMap<K, V> {
    val result = Object2ObjectOpenHashMap<K, V>(mapCapacity((this as? Collection)?.size ?: 10).coerceAtLeast(16))
    for (element in this) {
        val key = keySelector(element) ?: continue
        val value = valueSelector(element) ?: continue
        result[key] = value
    }
    return result
}

inline fun <reified R> Iterable<*>.filterFastIsInstance(): ObjectArrayList<R> {
    return filterIsInstanceTo(ObjectArrayList<R>())
}

inline fun <reified R> Iterable<*>.filterFastIsInstance(predicate: (R) -> Boolean): ObjectArrayList<R> {
    val result = ObjectArrayList<R>()
    for (element in this) if (element is R && predicate(element)) result.add(element)
    return result
}

inline fun <T> Iterable<T>.filterFast(predicate: (T) -> Boolean): ObjectArrayList<T> {
    return filterTo(ObjectArrayList<T>(), predicate)
}

inline fun <T> Iterable<T>.filterFastSet(predicate: (T) -> Boolean): ObjectOpenHashSet<T> {
    return filterTo(ObjectOpenHashSet<T>(), predicate)
}

inline fun <reified R> Sequence<*>.filterFastIsInstance(predicate: (R) -> Boolean): ObjectArrayList<R> {
    val result = ObjectArrayList<R>()
    for (element in this) if (element is R && predicate(element)) result.add(element)
    return result
}

inline fun <T> Sequence<T>.filterFast(predicate: (T) -> Boolean): ObjectArrayList<T> {
    return filterTo(ObjectArrayList<T>(), predicate)
}

inline fun <T> Sequence<T>.filterFastSet(predicate: (T) -> Boolean): ObjectOpenHashSet<T> {
    return filterTo(ObjectOpenHashSet<T>(), predicate)
}

inline fun <K, V> Map<out K, V>.filterFast(predicate: (Map.Entry<K, V>) -> Boolean): Object2ObjectOpenHashMap<K, V> {
    return filterTo(Object2ObjectOpenHashMap<K, V>(), predicate)
}

fun <K, V> Iterable<Pair<K, V>>.toFastMap(): Object2ObjectOpenHashMap<K, V> {
    if (this is Collection) {
        return when (size) {
            0 -> Object2ObjectOpenHashMap()
            1 -> fastMapOf(if (this is List) this[0] else iterator().next())
            else -> toMap(Object2ObjectOpenHashMap<K, V>(mapCapacity(size)))
        }
    }
    return toMap(LinkedHashMap<K, V>()).optimizeReadOnlyMap()
}

fun <K, V> fastMapOf(pair: Pair<K, V>): Object2ObjectOpenHashMap<K, V> = Object2ObjectOpenHashMap<K, V>().apply { put(pair.first, pair.second) }

fun mapCapacity(expectedSize: Int): Int = when {
    // We are not coercing the value to a valid one and not throwing an exception. It is up to the caller to
    // properly handle negative values.
    expectedSize < 0 -> expectedSize
    expectedSize < 3 -> expectedSize + 1
    expectedSize < INT_MAX_POWER_OF_TWO -> ((expectedSize / 0.75F) + 1.0F).toInt()
    // any large value
    else -> Int.MAX_VALUE
}

internal fun <K, V> Map<K, V>.optimizeReadOnlyMap() = when (size) {
    0 -> Object2ObjectOpenHashMap()
    1 -> Object2ObjectOpenHashMap(with(entries.iterator().next()) { Collections.singletonMap(key, value) })
    else -> Object2ObjectOpenHashMap(this)
}

private const val INT_MAX_POWER_OF_TWO: Int = 1 shl (Int.SIZE_BITS - 2)

fun <T> List<T>.plusFast(elements: Iterable<T>): ObjectArrayList<T> {
    if (elements is Collection) {
        val result = ObjectArrayList<T>(this.size + elements.size)
        result.addAll(this)
        result.addAll(elements)
        return result
    } else {
        val result = ObjectArrayList<T>(this)
        result.addAll(elements)
        return result
    }
}

fun <T> Set<T>.plusFast(elements: Iterable<T>): ObjectOpenHashSet<T> {
    if (elements is Collection) {
        val result = ObjectOpenHashSet<T>(this.size + elements.size)
        result.addAll(this)
        result.addAll(elements)
        return result
    } else {
        val result = ObjectOpenHashSet<T>(this)
        result.addAll(elements)
        return result
    }
}

inline fun <T, K> Iterable<T>.groupByFast(keySelector: (T) -> K): Object2ObjectLinkedOpenHashMap<K, ObjectArrayList<T>> {
    val map = Object2ObjectLinkedOpenHashMap<K, ObjectArrayList<T>>()
    for (element in this) {
        val key = keySelector(element)
        map.computeIfAbsent(key) { ObjectArrayList() }.add(element)
    }
    return map
}

inline fun <T, K> Iterable<T>.groupByFastSet(keySelector: (T) -> K): Object2ObjectLinkedOpenHashMap<K, ObjectOpenHashSet<T>> {
    val map = Object2ObjectLinkedOpenHashMap<K, ObjectOpenHashSet<T>>()
    for (element in this) {
        val key = keySelector(element)
        map.computeIfAbsent(key) { ObjectOpenHashSet() }.add(element)
    }
    return map
}

fun <T> List<T>.ensureSize(size: Int, default: T): MutableList<T> {
    val list = toMutableList()
    while (list.size < size) list.add(default)
    return list
}