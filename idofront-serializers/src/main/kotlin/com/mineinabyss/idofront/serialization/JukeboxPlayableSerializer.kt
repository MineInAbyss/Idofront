package com.mineinabyss.idofront.serialization

import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.kyori.adventure.key.Key
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.components.FoodComponent
import org.bukkit.inventory.meta.components.JukeboxPlayableComponent

@Serializable
@SerialName("JukeboxPlayable")
private class JukeboxPlayableSurrogate(
    val song: @Serializable(KeySerializer::class) Key,
    val showInTooltip: Boolean = true
) {
    init {
        val registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.JUKEBOX_SONG)
        require(registry.get(song) != null) { "Invalid song $song, valid ones are ${registry.map { it.key() }.joinToString()}" }
    }
}

object JukeboxPlayableSerializer : KSerializer<JukeboxPlayableComponent> {
    override val descriptor: SerialDescriptor = JukeboxPlayableSurrogate.serializer().descriptor
    override fun serialize(encoder: Encoder, value: JukeboxPlayableComponent) {
        val surrogate = JukeboxPlayableSurrogate(value.songKey, value.isShowInTooltip)
        encoder.encodeSerializableValue(JukeboxPlayableSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): JukeboxPlayableComponent {
        return ItemStack(Material.MUSIC_DISC_CREATOR).itemMeta.jukeboxPlayable.apply {
            val surrogate = decoder.decodeSerializableValue(JukeboxPlayableSurrogate.serializer())
            songKey = NamespacedKey.fromString(surrogate.song.asString())!!
            isShowInTooltip = surrogate.showInTooltip
        }
    }
}