package com.mineinabyss.idofront.autoscan

import com.mineinabyss.idofront.messaging.logWarn
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import org.reflections.util.FilterBuilder
import kotlin.reflect.KClass

/**
 * Helper class around the java reflections library that includes some caching of classpaths.
 *
 * A [path] to limit search to may be specified. Specific packages can also be excluded with [excludePath].
 *
 * _Note that autoscan may not work with some custom classloading solutions._
 *
 * @property path Optional path to restrict what packages are scanned.
 * @property excluded Excluded paths under [path].
 */
class AutoScanner(
    private val classLoader: ClassLoader,
    val path: String? = null,
    val excluded: List<String> = listOf()
) {

    /** Gets a reflections object under [path], caching it for faster follow-up lookups. */
    fun getReflections(): Reflections? {
        // cache the object we get because it takes considerable amount of time to get
        val cacheKey = CacheKey(classLoader, path, excluded)
        reflectionsCache[cacheKey]?.let { return it }

        val reflections = Reflections(
            ConfigurationBuilder()
                .addClassLoader(classLoader)
                .addUrls(ClasspathHelper.forClassLoader(classLoader))
                .addScanners(SubTypesScanner())
                .filterInputsBy(FilterBuilder().apply {
                    if (path != null) includePackage(path)
                    excluded.forEach { excludePackage(it) }
                })
        )

        reflectionsCache[cacheKey] = reflections

        // Check if the store is empty. Since we only use a single SubTypesScanner, if this is empty
        // then the path passed in returned 0 matches.
        if (reflections.store.keySet().isEmpty()) {
            logWarn("Autoscanner failed to find classes for ${classLoader}${if (path == null) "" else " in package ${path}}"}.")
            return null
        }
        return reflections
    }

    /** Gets all subclasses of type [T] on the classpath. */
    inline fun <reified T : Any> getSubclassesOf(): List<KClass<out T>> {
        return getReflections()?.getSubTypesOf(T::class.java)?.map { it.kotlin } ?: listOf()
    }

    private companion object {
        data class CacheKey(val classLoader: ClassLoader, val path: String?, val excluded: Collection<String>)

        val reflectionsCache = mutableMapOf<CacheKey, Reflections>()
    }
}
