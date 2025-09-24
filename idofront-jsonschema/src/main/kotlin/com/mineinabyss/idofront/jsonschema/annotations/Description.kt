package com.mineinabyss.idofront.jsonschema.annotations

import kotlinx.serialization.SerialInfo

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.BINARY)
@SerialInfo
annotation class Description(
    val description: String
)
