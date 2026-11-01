package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.serialization.helpers.suggestKeys
import com.mineinabyss.idofront.serialization.helpers.typedKeyNames
import com.mineinabyss.jsonschema.dsl.JsonSchemaDescriptor
import com.mineinabyss.jsonschema.dsl.SchemaContext
import com.mineinabyss.jsonschema.dsl.SchemaProperty
import com.mineinabyss.jsonschema.dsl.SchemaType
import io.papermc.paper.registry.keys.BlockTypeKeys
import io.papermc.paper.registry.keys.ItemTypeKeys
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Material


object MaterialByNameSerializer : KSerializer<Material>, JsonSchemaDescriptor {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("com.mineinabyss.Material", PrimitiveKind.STRING)

    context(context: SchemaContext)
    override fun SchemaProperty.defineSchema() {
        suggestKeys(typedKeyNames(ItemTypeKeys::class.java) + typedKeyNames(BlockTypeKeys::class.java)) {
            type = SchemaType.STRING
        }
    }

    override fun serialize(encoder: Encoder, value: Material) {
        encoder.encodeString(value.key.toString())
    }

    override fun deserialize(decoder: Decoder): Material {
        val name = decoder.decodeString()
        return Material.matchMaterial(name) ?: error("Material $name does not exist")
    }
}
