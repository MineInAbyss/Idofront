package com.mineinabyss.idofront.jsonschema.generator.kotlinx_serialization

import com.charleskorn.kaml.YamlComment
import com.mineinabyss.idofront.jsonschema.annotations.Description
import com.mineinabyss.idofront.jsonschema.dsl.JsonSchemaDefinedDescriptor
import com.mineinabyss.idofront.jsonschema.dsl.SchemaContext
import com.mineinabyss.idofront.jsonschema.dsl.SchemaProperty
import com.mineinabyss.idofront.jsonschema.dsl.propertyBuilder
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.descriptors.elementDescriptors
import kotlinx.serialization.descriptors.elementNames
import kotlinx.serialization.descriptors.nonNullOriginal

data class KotlinxSerializationJsonSchemaGenerator(
    val replaceDescriptors: (SerialDescriptor) -> SerialDescriptor = { it },
    val processAnnotations: SchemaProperty.(List<Annotation>) -> Unit = {
        applyDescriptionFromCommonAnnotations(it)
    },
) {
    context(schema: SchemaContext, property: SchemaProperty)
    fun applyClassDescriptor(originalDescriptor: SerialDescriptor): Unit = with(property) {
        val descriptor = replaceDescriptors(originalDescriptor.nonNullOriginal).nonNullOriginal
        schema.defsForSerialDescriptors[descriptor]?.let {
            ref = $$"#/$defs/$$it"
            return
        }
        schema.defs[descriptor.serialName]?.let {
            ref = $$"#/$defs/$${descriptor.serialName}"
            return
        }
        val childElements = descriptor.elementNames.zip(descriptor.elementDescriptors)

        if (descriptor is JsonSchemaDefinedDescriptor) {
            ref = schema.definition(descriptor) {
                descriptor.define(this)
            }
            return
        }
        if (descriptor.isInline) {
            val entry = childElements.firstOrNull() ?: return
            applyClassDescriptor(entry.second)
            return@with
        }

        when (descriptor.kind) {
            SerialKind.CONTEXTUAL -> {
                ref = $$"#/$defs/$${descriptor.serialName}"
            }

            StructureKind.MAP -> {
                val keys = childElements.first()
                val values = childElements.last()
                //TODO does json schema support non-string keys?
                additionalProperties {
                    applyClassDescriptor(values.second)
                }
            }

            StructureKind.OBJECT -> {
            }

            PolymorphicKind.OPEN -> {
                println("Open")
            }

            PolymorphicKind.SEALED -> {
                if (childElements.size != 2) {
                    println("Warn: $descriptor was sealed but does not have 2 elements")
                    return
                }
                val (key, sealedDescriptor) = childElements.map { it.second }
                // Definition forSealed<MyTopClassDefinition>
                schema.definition(sealedDescriptor.serialName, sealedDescriptor) {
                    anyOf(
                        sealedDescriptor.elementDescriptors.map { subclass ->
                            val subclassReference = "${sealedDescriptor.serialName}.${subclass.serialName}"
                            propertyBuilder {
                                // Create definition for individual subclasses
                                schema.definition(subclassReference, subclass, override = true) {
                                    property("type", required = true) {
                                        type = STRING
                                        const = subclass.serialName
                                    }
                                    applyClassDescriptor(subclass)
                                }
                                ref = $$"#/$defs/$${subclassReference}"
//                                allOf(
//                                    { ref = $$"#/$defs/$${subclassReference}" },
//                                    {
//                                        // We only apply this type when "type" discriminator matches this serial name
//                                        property("type", required = true) {
//                                            type = STRING
//                                            const = subclass.serialName
//                                        }
//                                    }
//                                )
                                additionalPropertiesAllowed = false
                            }
                        }
                    )
                }
                ref = $$"#/$defs/$${sealedDescriptor.serialName}"
            }


            StructureKind.LIST -> {
                type = ARRAY
                items {
                    applyClassDescriptor(descriptor.getElementDescriptor(0))
                }
            }

            StructureKind.CLASS -> {
                type = OBJECT
                childElements.forEachIndexed { index, (name, childElement) ->
                    val propertyAnnotations = descriptor.getElementAnnotations(index)
                    property(
                        name,
                        required = !descriptor.isElementOptional(index),
                        nullable = false,
                        //                nullable = element.isNullable,
                    ) {
                        processAnnotations(propertyAnnotations)
                        applyClassDescriptor(childElement.nonNullOriginal) //TODO handle nulls correctly
                    }
                }
            }

            PrimitiveKind.STRING -> type = STRING
            PrimitiveKind.CHAR -> type = STRING
            PrimitiveKind.INT -> type = INTEGER
            PrimitiveKind.BYTE -> type = INTEGER
            PrimitiveKind.SHORT -> type = INTEGER
            PrimitiveKind.LONG -> type = NUMBER
            PrimitiveKind.FLOAT -> type = NUMBER
            PrimitiveKind.DOUBLE -> type = NUMBER
            PrimitiveKind.BOOLEAN -> type = BOOLEAN
            SerialKind.ENUM -> {
                schema.definition(descriptor.serialName, descriptor) {
                    enum = descriptor.elementNames.toMutableList()
                }
                ref = $$"#/$defs/$${descriptor.serialName}"
            }

            else -> {
                println("Unknown SerialKind: ${descriptor.kind}")
            }
        }
    }

    companion object {
        fun SchemaProperty.applyDescriptionFromCommonAnnotations(annotations: List<Annotation>) {
            val desc = annotations.firstNotNullOfOrNull {
                when (it) {
                    is YamlComment -> it.lines.joinToString("\\n")
                    is Description -> it.description
                    else -> null
                }
            }
            if (desc != null) description = desc
        }
    }
}