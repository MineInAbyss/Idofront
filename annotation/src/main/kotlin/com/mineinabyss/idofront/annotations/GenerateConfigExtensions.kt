package com.mineinabyss.idofront.annotations

/**
 * Generates extension functions for accessing constructor parameters of a class inside. By default will look for a
 * class named `Data`.
 *
 * Requires a property defined by [property] to exist within the class, for the extension functions to go through.
 *
 * @param insideClassName The name of the class to look for inside.
 * @param property The name of the property via which to access parameters.
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class GenerateConfigExtensions(val insideClassName: String = "Data", val property: String = "data")