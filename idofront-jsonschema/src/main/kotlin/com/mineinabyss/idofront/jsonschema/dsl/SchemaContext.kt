package com.mineinabyss.idofront.jsonschema.dsl

import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import kotlinx.serialization.serializer

@DslMarker
annotation class SchemaDSLMarker

@SchemaDSLMarker
interface Schema

class SchemaContext {
    val defsForSerialDescriptors = mutableMapOf<SerialDescriptor, String>()
    val defs = mutableMapOf<String, SchemaProperty>()
    val schema = "https://json-schema.org/draft/2020-12/schema"

    var rootProperty: SchemaProperty = SchemaProperty()

    fun rootProperty(init: SchemaProperty.() -> Unit) {
        rootProperty = SchemaProperty().apply(init)
    }

    inline fun <reified T> definition(crossinline init: SchemaProperty.(descriptor: SerialDescriptor) -> Unit): String {
        val desc = serializer<T>().descriptor
        return definition(desc.serialName, desc) {
            init(desc)
        }
    }

    fun definition(serialDescriptor: SerialDescriptor, override: Boolean = false, init: SchemaProperty.() -> Unit): String {
        return definition(serialDescriptor.serialName, serialDescriptor, override, init)
    }

    fun definition(name: String, serialDescriptor: SerialDescriptor?, override: Boolean = false, init: SchemaProperty.() -> Unit): String {
        val ref = $$"#/$defs/$$name"
        if (!override && name in defs) return ref
        defs[name] = SchemaProperty().apply(init)
        if (serialDescriptor != null) defsForSerialDescriptors[serialDescriptor] = name
        return ref
    }

    fun build(): JsonObject = buildJsonObject {
        rootProperty.build().entries.forEach { (key, value) ->
            put(key, value)
        }
        put($$"$schema", schema)
        putJsonObject($$"$defs") {
            defs.forEach { (name, def) ->
                put(name, def.build())
            }
        }
    }
}

fun jsonSchema(init: SchemaContext.() -> Unit): JsonObject {
    return SchemaContext().apply(init).build()
}

context(schema: SchemaContext)
fun propertyBuilder(init: SchemaProperty.() -> Unit): SchemaProperty.() -> Unit = init
