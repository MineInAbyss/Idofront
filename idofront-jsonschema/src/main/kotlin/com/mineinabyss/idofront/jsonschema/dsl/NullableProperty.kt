package com.mineinabyss.idofront.jsonschema.dsl

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class NullableProperty(
    val inner: SchemaProperty,
) : SchemaProperty() {
    override fun build(): JsonObject = buildJsonObject {
        put("anyOf", buildJsonArray {
            add(buildJsonObject {
                put("type", "null")
            })
            add(inner.build())
        })
    }
}