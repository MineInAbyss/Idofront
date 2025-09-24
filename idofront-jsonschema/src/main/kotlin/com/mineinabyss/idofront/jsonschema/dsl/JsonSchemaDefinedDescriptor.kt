package com.mineinabyss.idofront.jsonschema.dsl

import kotlinx.serialization.SealedSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor

interface JsonSchemaDefinedDescriptor {
    context(context: SchemaContext)
    fun define(property: SchemaProperty)
}

@OptIn(SealedSerializationApi::class)
fun SerialDescriptor.withJsonSchema(schema: SchemaProperty.() -> Unit): SerialDescriptor {
    return object : SerialDescriptor by this, JsonSchemaDefinedDescriptor {
        context(context: SchemaContext)
        override fun define(property: SchemaProperty) = schema(property)
    }
}