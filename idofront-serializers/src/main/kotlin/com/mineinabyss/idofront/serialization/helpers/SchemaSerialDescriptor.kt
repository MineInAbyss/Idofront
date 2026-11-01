@file:OptIn(ExperimentalSerializationApi::class, SealedSerializationApi::class)

package com.mineinabyss.idofront.serialization.helpers

import com.mineinabyss.jsonschema.dsl.JsonSchemaDescriptor
import com.mineinabyss.jsonschema.dsl.SchemaContext
import com.mineinabyss.jsonschema.dsl.SchemaProperty
import com.mineinabyss.jsonschema.dsl.SchemaType
import io.papermc.paper.registry.TypedKey
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SealedSerializationApi
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.elementNames
import kotlinx.serialization.descriptors.nonNullOriginal
import java.lang.reflect.Modifier

/**
 * Carries a schema on the descriptor itself, so it applies wherever the descriptor ends up.
 * Used by open serializers whose subclasses or wrappers (nullable, delegation) would not be found by scanning for serializers
 */
internal class SchemaSerialDescriptor(
    original: SerialDescriptor,
    private val schema: context(SchemaContext) SchemaProperty.() -> Unit,
) : SerialDescriptor by original, JsonSchemaDescriptor {
    context(context: SchemaContext)
    override fun SchemaProperty.defineSchema() = schema(context, this)
}

internal fun SerialDescriptor.withSchema(schema: context(SchemaContext) SchemaProperty.() -> Unit): SerialDescriptor =
    SchemaSerialDescriptor(this, schema)

const val NAMESPACED_KEY_PATTERN = "^[a-z0-9_.-]+:[a-z0-9_./-]+$"

/** Names of the [TypedKey] constants on one of Paper's generated key classes, such as `MobEffectKeys` */
fun typedKeyNames(keysClass: Class<*>): List<String> = keysClass.fields
    .filter { Modifier.isStatic(it.modifiers) && TypedKey::class.java.isAssignableFrom(it.type) }
    .map { (it.get(null) as TypedKey<*>).asString() }

/** Suggests [knownKeys] while still accepting anything matching [fallback], such as keys added by datapacks */
fun SchemaProperty.suggestKeys(knownKeys: Collection<String>, fallback: SchemaProperty.() -> Unit) {
    if (knownKeys.isEmpty()) return fallback()
    anyOf(
        {
            type = SchemaType.STRING
            enum += knownKeys.distinct().sorted()
        },
        fallback,
    )
}

/** Mirrors how the schema generator treats [serializer], for schemas that wrap another serializer */
context(context: SchemaContext)
internal fun SchemaProperty.schemaOf(serializer: KSerializer<*>) {
    val descriptor = serializer.descriptor.nonNullOriginal
    val schemaDescriptor = serializer as? JsonSchemaDescriptor ?: descriptor as? JsonSchemaDescriptor
    if (schemaDescriptor != null) {
        ref = context.definition(descriptor) { with(schemaDescriptor) { defineSchema() } }
        return
    }
    val primitive = if (descriptor.isInline) descriptor.getElementDescriptor(0).kind else descriptor.kind
    when (primitive) {
        PrimitiveKind.STRING, PrimitiveKind.CHAR -> type = SchemaType.STRING
        PrimitiveKind.BOOLEAN -> type = SchemaType.BOOLEAN
        PrimitiveKind.BYTE, PrimitiveKind.SHORT, PrimitiveKind.INT, PrimitiveKind.LONG -> type = SchemaType.INTEGER
        PrimitiveKind.FLOAT, PrimitiveKind.DOUBLE -> type = SchemaType.NUMBER
        SerialKind.ENUM -> {
            type = SchemaType.STRING
            enum += descriptor.elementNames
        }
        else -> {
            context.requestDefinition(descriptor)
            ref = "#/\$defs/${descriptor.serialName}"
        }
    }
}
