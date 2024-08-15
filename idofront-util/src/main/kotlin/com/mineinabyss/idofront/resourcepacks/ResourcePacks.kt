package com.mineinabyss.idofront.resourcepacks

import com.mineinabyss.idofront.messaging.idofrontLogger
import net.kyori.adventure.key.Key
import org.bukkit.Material
import team.unnamed.creative.ResourcePack
import team.unnamed.creative.metadata.overlays.OverlaysMeta
import team.unnamed.creative.model.Model
import team.unnamed.creative.serialize.minecraft.MinecraftResourcePackReader
import team.unnamed.creative.serialize.minecraft.MinecraftResourcePackWriter
import team.unnamed.creative.sound.SoundRegistry
import java.io.File

object ResourcePacks {
    val resourcePackWriter = MinecraftResourcePackWriter.builder().prettyPrinting(true).build()
    val resourcePackReader = MinecraftResourcePackReader.builder().lenient(true).build()

    /**
     * Loads a copy of the vanilla resourcepack for the current version
     * If it has not been yet, it will first be extracted locally
     * The ResourcePack instance does not contain any of the vanilla OGG files due to filesize optimizations
     */
    val defaultVanillaResourcePack by lazy {
        MinecraftAssetExtractor.assetPath.apply { MinecraftAssetExtractor.extractLatest() }.let(::readToResourcePack)
    }

    /**
     * Returns the Model used in #defaultVanillaResourcePack by the given Material
     */
    fun vanillaModelForMaterial(material: Material): Model? {
        return defaultVanillaResourcePack?.model(vanillaKeyForMaterial(material))
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

        mergePack.models().forEach { model ->
            val baseModel = originalPack.model(model.key()) ?: return@forEach originalPack.model(model)
            originalPack.model(ensureItemOverridesSorted(model.apply { overrides().addAll(baseModel.overrides()) }))
        }
        mergePack.fonts().forEach { font ->
            val baseFont = originalPack.font(font.key()) ?: return@forEach originalPack.font(font)
            originalPack.font(baseFont.apply { providers().addAll(font.providers()) })
        }
        mergePack.soundRegistries().forEach { soundRegistry ->
            val baseRegistry = originalPack.soundRegistry(soundRegistry.namespace()) ?: return@forEach originalPack.soundRegistry(soundRegistry)
            originalPack.soundRegistry(
                SoundRegistry.soundRegistry(
                    soundRegistry.namespace(),
                    baseRegistry.sounds().toMutableSet().apply { addAll(soundRegistry.sounds()) })
            )
        }
        mergePack.atlases().forEach { atlas ->
            val baseAtlas = originalPack.atlas(atlas.key())?.toBuilder() ?: return@forEach originalPack.atlas(atlas)
            atlas.sources().forEach(baseAtlas::addSource)
            originalPack.atlas(baseAtlas.build())
        }
        mergePack.languages().forEach { language ->
            val baseLanguage = originalPack.language(language.key()) ?: return@forEach originalPack.language(language)
            originalPack.language(baseLanguage.apply { translations().putAll(language.translations()) })
        }
        mergePack.blockStates().forEach { blockState ->
            val baseBlockState = originalPack.blockState(blockState.key()) ?: return@forEach originalPack.blockState(blockState)
            originalPack.blockState(baseBlockState.apply { variants().putAll(blockState.variants()) })
        }

        if (originalPack.packMeta()?.description().isNullOrEmpty()) mergePack.packMeta()?.let { originalPack.packMeta(it) }
        if (mergePack.overlaysMeta() != null) {
            val originalOverlays = originalPack.overlaysMeta()?.entries() ?: emptyList()
            val mergeOverlays = mergePack.overlaysMeta()?.entries() ?: emptyList()

            originalPack.overlaysMeta(OverlaysMeta.of(originalOverlays.plus(mergeOverlays)))
        }
        if (originalPack.icon() == null) mergePack.icon()?.let { originalPack.icon(it) }
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
        val vanillaModel = defaultVanillaResourcePack?.model(model.key()) ?: return model
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