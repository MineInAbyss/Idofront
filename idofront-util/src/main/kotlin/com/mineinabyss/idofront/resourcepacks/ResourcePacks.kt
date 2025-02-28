package com.mineinabyss.idofront.resourcepacks

import com.mineinabyss.idofront.messaging.idofrontLogger
import net.kyori.adventure.key.Key
import org.bukkit.Material
import team.unnamed.creative.ResourcePack
import team.unnamed.creative.blockstate.BlockState
import team.unnamed.creative.font.Font
import team.unnamed.creative.lang.Language
import team.unnamed.creative.metadata.overlays.OverlayEntry
import team.unnamed.creative.metadata.overlays.OverlaysMeta
import team.unnamed.creative.metadata.sodium.SodiumMeta
import team.unnamed.creative.model.Model
import team.unnamed.creative.serialize.minecraft.MinecraftResourcePackReader
import team.unnamed.creative.serialize.minecraft.MinecraftResourcePackWriter
import team.unnamed.creative.sound.SoundRegistry
import java.io.File
import kotlin.collections.component1
import kotlin.collections.component2

object ResourcePacks {
    val resourcePackWriter = MinecraftResourcePackWriter.builder().prettyPrinting(false).build()
    val resourcePackReader = MinecraftResourcePackReader.builder().lenient(true).build()

    /**
     * Loads a copy of the vanilla resourcepack for the current version
     * If it has not been yet, it will first be extracted locally
     * The ResourcePack instance does not contain any of the vanilla OGG files due to filesize optimizations
     */
    val defaultVanillaResourcePack by lazy {
        MinecraftAssetExtractor.assetPath.apply { MinecraftAssetExtractor.extractLatest() }.let(::readToResourcePack) ?: ResourcePack.resourcePack()
    }

    /**
     * Returns the Model used in #defaultVanillaResourcePack by the given Material
     */
    fun vanillaModelForMaterial(material: Material): Model? {
        return defaultVanillaResourcePack.model(vanillaKeyForMaterial(material))
    }

    /**
     * Returns the vanilla Key used for the item-model
     */
    fun vanillaKeyForMaterial(material: Material): Key {
        return Key.key(when (material) {
            Material.CROSSBOW -> "item/crossbow_standby"
            Material.SPYGLASS -> "item/spyglass_in_hand"
            Material.TRIDENT -> "item/trident_in_hand"
            else -> "item/${material.key().value()}"
        })
    }

    fun readToResourcePack(file: File): ResourcePack? {
        return runCatching {
            when {
                !file.exists() -> null
                file.isDirectory && !file.listFiles().isNullOrEmpty() -> resourcePackReader.readFromDirectory(file)
                file.extension == "zip" -> resourcePackReader.readFromZipFile(file)
                else -> null
            }
        }.onFailure { it.printStackTrace()/*idofrontLogger.w(file.name + ": " + it.message)*/ }.getOrNull()
    }

    fun writeToFile(file: File, resourcePack: ResourcePack) {
        when {
            file.extension == "zip" -> resourcePackWriter.writeToZipFile(file, resourcePack)
            file.isDirectory -> resourcePackWriter.writeToDirectory(file, resourcePack)
            else -> {
                idofrontLogger.w("Failed to generate resourcepack in ${file.path}")
            }
        }
    }

    /**
     * Merges the content of two ResourcePacks, handling conflicts where possible
     * Will sort ItemOverrides for models
     */
    fun mergeResourcePacks(originalPack: ResourcePack, mergePack: ResourcePack) {
        mergePack.textures().forEach(originalPack::texture)
        mergePack.sounds().forEach(originalPack::sound)
        mergePack.unknownFiles().forEach(originalPack::unknownFile)

        (mergePack.packMeta() ?: originalPack.packMeta())?.apply(originalPack::packMeta)
        (mergePack.icon() ?: originalPack.icon())?.apply(originalPack::icon)

        (originalPack.overlaysMeta()?.entries() ?: mutableListOf<OverlayEntry>())
            .plus(mergePack.overlaysMeta()?.entries() ?: mutableListOf<OverlayEntry>())
            .also { originalPack.overlaysMeta(OverlaysMeta.of(it)) }

        (originalPack.sodiumMeta()?.ignoredShaders() ?: mutableListOf<String>())
            .plus(mergePack.sodiumMeta()?.ignoredShaders() ?: mutableListOf<String>())
            .also { originalPack.sodiumMeta(SodiumMeta.of(it)) }

        mergePack.models().forEach { model: Model ->
            model.toBuilder().apply {
                originalPack.model(model.key())?.overrides()?.forEach(::addOverride)
            }.build().addTo(originalPack)
        }

        mergePack.fonts().forEach { font: Font ->
            font.toBuilder().apply {
                originalPack.font(font.key())?.providers()?.forEach(::addProvider)
            }.build().addTo(originalPack)
        }

        mergePack.soundRegistries().forEach { soundRegistry: SoundRegistry ->
            val baseRegistry = originalPack.soundRegistry(soundRegistry.namespace())
            if (baseRegistry != null) {
                val mergedEvents = LinkedHashSet(baseRegistry.sounds())
                mergedEvents.addAll(soundRegistry.sounds())
                SoundRegistry.soundRegistry(baseRegistry.namespace(), mergedEvents).addTo(originalPack)
            } else soundRegistry.addTo(originalPack)
        }

        mergePack.atlases().forEach { atlas ->
            atlas.toBuilder().apply {
                originalPack.atlas(atlas.key())?.sources()?.forEach(::addSource)
            }.build().addTo(originalPack)
        }

        mergePack.languages().forEach { language: Language ->
            originalPack.language(language.key())?.let { base: Language ->
                base.translations().entries.forEach { (key, value) ->
                    language.translations().putIfAbsent(key, value)
                }
            }
            language.addTo(originalPack)
        }

        mergePack.blockStates().forEach { blockState: BlockState ->
            originalPack.blockState(blockState.key())?.let { base: BlockState ->
                blockState.multipart().addAll(base.multipart())
                blockState.variants().putAll(base.variants())
            }
            blockState.addTo(originalPack)
        }
    }

    /**
     * Ensures that a Model's overrides are sorted based on the CustomModelData predicate
     * Returns the Model with any present overrides sorted
     */
    fun ensureItemOverridesSorted(model: Model): Model {
        val sortedOverrides = (model.overrides().takeIf { it.isNotEmpty() } ?: return model).sortedBy { override ->
            // value() is a LazilyParsedNumber so convert it to an Int
            override.predicate().find { it.name() == "custom_model_data" }?.value()?.toString()?.toIntOrNull() ?: 0
        }

        return model.toBuilder().overrides(sortedOverrides).build()
    }

    /**
     * Ensure that vanilla models have all their properties set
     * Returns a new Model with all vanilla properties set, or the original Model if it was not a vanilla model
     */
    fun ensureVanillaModelProperties(model: Model): Model {
        val vanillaModel = defaultVanillaResourcePack.model(model.key()) ?: return model
        val builder = model.toBuilder()

        if (model.textures().let { it.variables().isEmpty() && it.layers().isEmpty() && it.particle() == null })
            builder.textures(vanillaModel.textures())
        if (model.elements().isEmpty()) builder.elements(vanillaModel.elements())
        if (model.overrides().isEmpty()) builder.overrides(vanillaModel.overrides())
        if (model.display().isEmpty()) builder.display(vanillaModel.display())
        if (model.guiLight() == null) builder.guiLight(vanillaModel.guiLight())
        if (model.parent() == null) builder.parent(vanillaModel.parent())
        if (!model.ambientOcclusion()) builder.ambientOcclusion(vanillaModel.ambientOcclusion())

        return ensureItemOverridesSorted(builder.build())
    }
}