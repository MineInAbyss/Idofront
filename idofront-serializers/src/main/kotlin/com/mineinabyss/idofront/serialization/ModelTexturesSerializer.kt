package com.mineinabyss.idofront.serialization

import com.charleskorn.kaml.*
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.kyori.adventure.key.Key
import team.unnamed.creative.model.ModelTexture
import team.unnamed.creative.model.ModelTextures

@Serializable
@SerialName("ModelTextures")
class ModelTexturesSurrogate(
    val layers: List<@Serializable(KeySerializer::class) Key> = emptyList(),
    val particle: @Serializable(KeySerializer::class) Key? = null,
    val variables: Map<String, @Serializable(KeySerializer::class) Key> = emptyMap(),
) {
    @Transient val modelTextures = ModelTextures.of(layers.map(ModelTexture::ofKey), particle?.let(ModelTexture::ofKey), variables.mapValues { ModelTexture.ofKey(it.value) }.toMap())
    @Transient val isEmpty = layers.isEmpty() && modelTextures.variables().isEmpty() && particle == null
}

object ModelTexturesSerializer : KSerializer<ModelTexturesSurrogate> {
    override val descriptor: SerialDescriptor = ContextualSerializer(ModelTexturesSurrogate::class).descriptor

    override fun deserialize(decoder: Decoder): ModelTexturesSurrogate {
        val node = (decoder as? YamlInput)?.node?.yamlMap?.get<YamlNode>("textures") ?: return ModelTexturesSurrogate.serializer().deserialize(decoder)

        return when (node) {
            is YamlScalar -> ModelTexturesSurrogate(mutableListOf(Key.key(node.content)))
            is YamlList -> ModelTexturesSurrogate(node.items.mapNotNull { (it as? YamlScalar)?.content?.let(Key::key) }.toMutableList())
            is YamlMap -> ModelTexturesSurrogate(variables = node.entries.mapNotNull { it.key.content to ((it.value as? YamlScalar)?.content?.let(Key::key) ?: return@mapNotNull null) }.toMap())
            else -> ModelTexturesSurrogate()
        }
    }

    override fun serialize(encoder: Encoder, value: ModelTexturesSurrogate) {
        ModelTexturesSurrogate.serializer().serialize(encoder, value)
    }
}