package com.mineinabyss.idofront.jsonschema.dsl

import com.mineinabyss.idofront.jsonschema.dsl.SchemaType.OBJECT
import com.mineinabyss.idofront.jsonschema.dsl.SchemaType.STRING
import kotlinx.serialization.json.*

typealias SchemaPropertyBuilder = SchemaProperty.() -> Unit

open class SchemaProperty : Schema {
    var description: String? = null
    var title: String? = null
    var ref: String? = null
    var type: SchemaType = OBJECT
    var pattern: String? = null

    val required: MutableSet<String> = mutableSetOf()
    val properties = mutableMapOf<String, SchemaProperty>()

    var items: SchemaProperty? = null
        private set
    var additionalProperties: SchemaProperty? = null
        private set
    var propertyNames: SchemaProperty? = null
        private set

    var additionalPropertiesAllowed: Boolean = true
    var enum = mutableListOf<String>()
    var anyOf = mutableListOf<SchemaProperty>()
    var allOf = mutableListOf<SchemaProperty>()
    var oneOf = mutableListOf<SchemaProperty>()
    var const: String? = null

    fun property(
        name: String,
        required: Boolean = false,
        nullable: Boolean = false,
        init: SchemaProperty.() -> Unit,
    ) {
        if (required) this.required += name
        properties[name] = SchemaProperty().apply(init).let { if (nullable) NullableProperty(it) else it }
    }


    fun anyOf(vararg properties: SchemaPropertyBuilder) = anyOf(properties.toList())

    fun allOf(vararg properties: SchemaPropertyBuilder) = allOf(properties.toList())

    fun anyOf(properties: Collection<SchemaPropertyBuilder>) = properties.forEach { anyOf.add(SchemaProperty().apply(it)) }

    fun allOf(properties: Collection<SchemaPropertyBuilder>) = properties.forEach { allOf.add(SchemaProperty().apply(it)) }

    fun items(init: SchemaProperty.() -> Unit) {
        items = SchemaProperty().apply(init)
    }

    fun additionalProperties(init: SchemaProperty.() -> Unit) {
        additionalProperties = SchemaProperty().apply(init)
    }

    fun propertyNames(init: SchemaProperty.() -> Unit) {
        propertyNames = SchemaProperty().apply(init)
    }


    open fun build(): JsonObject = buildJsonObject {
        when {
            anyOf.isNotEmpty() -> putJsonArray("anyOf") { addAll(anyOf.map { it.build() }) }
            allOf.isNotEmpty() -> putJsonArray("allOf") { addAll(allOf.map { it.build() }) }
            oneOf.isNotEmpty() -> putJsonArray("oneOf") { addAll(oneOf.map { it.build() }) }
            enum.isNotEmpty() -> putJsonArray("enum") { addAll(enum) }
            ref != null -> put($$"$ref", ref)
            else -> {
                put("type", type.typeName)

                if (type == STRING && pattern != null) {
                    put("pattern", pattern)
                }
                if (type == OBJECT) {
                    if (required.isNotEmpty()) putJsonArray("required") { addAll(required) }
                    if (properties.isNotEmpty()) putJsonObject("properties") {
                        properties.forEach { (name, def) ->
                            put(name, def.build())
                        }
                    }
                }
            }
        }
        propertyNames?.let { put("propertyNames", it.build()) }
        if (additionalPropertiesAllowed)
            additionalProperties?.let { put("additionalProperties", it.build()) }
        else {
            put("additionalProperties", false)
        }
        items?.let { put("items", it.build()) }
        description?.let { put("description", it) }
        title?.let { put("title", it) }
        const?.let { put("const", it) }
    }
}