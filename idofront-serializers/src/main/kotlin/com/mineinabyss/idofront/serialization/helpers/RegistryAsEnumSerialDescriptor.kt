package com.mineinabyss.idofront.serialization.helpers

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import org.bukkit.Registry

@OptIn(InternalSerializationApi::class)
fun RegistryAsEnumSerialDescriptor(
    serialName: String,
    registry: Registry<*>,
) = buildSerialDescriptor(serialName, SerialKind.ENUM) {
    val keys = registry.stream().toList().map { it.key.asString() }
    keys.forEach {
        element(it, buildSerialDescriptor(it, StructureKind.OBJECT))
    }
}

@OptIn(InternalSerializationApi::class)
fun ListAsEnumSerialDescriptor(serialName: String, options: Collection<String>) = buildSerialDescriptor(serialName, SerialKind.ENUM) {
    options.forEach {
        element(it, buildSerialDescriptor(it, StructureKind.OBJECT))
    }
}