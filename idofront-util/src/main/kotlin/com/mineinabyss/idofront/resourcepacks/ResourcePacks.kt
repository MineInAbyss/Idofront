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
import team.unnamed.creative.item.CompositeItemModel
import team.unnamed.creative.item.ConditionItemModel
import team.unnamed.creative.item.EmptyItemModel
import team.unnamed.creative.item.Item
import team.unnamed.creative.item.ItemModel
import team.unnamed.creative.item.RangeDispatchItemModel
import team.unnamed.creative.item.ReferenceItemModel
import team.unnamed.creative.item.SelectItemModel
import team.unnamed.creative.metadata.Metadata
import team.unnamed.creative.overlay.Overlay
import team.unnamed.creative.overlay.ResourceContainer
import kotlin.collections.component1
import kotlin.collections.component2

object ResourcePacks {
    val EMPTY_MODEL = Key.key("minecraft:empty")

    val resourcePackWriter = MinecraftResourcePackWriter.builder().prettyPrinting(false).build()
    val resourcePackReader = MinecraftResourcePackReader.builder().lenient(true).build()

    /**
     * Loads a copy of the vanilla resourcepack for the current version
     * If it has not been yet, it will first be extracted locally
     * The ResourcePack instance does not contain any of the vanilla OGG files due to filesize optimizations
     */
    val vanillaResourcePack by lazy {
        val resourcePack = MinecraftAssetExtractor.zipPath.apply { MinecraftAssetExtractor.extractLatest() }
            .let(::readToResourcePack) ?: ResourcePack.resourcePack()
        resourcePack.item(Item.item(EMPTY_MODEL, ItemModel.empty()))
        resourcePack
    }
    val vanillaSounds = mutableListOf<Key>()

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

    fun overwriteResourcePack(resourcePack: ResourcePack, overwritePack: ResourcePack) {
        clearResourcePack(resourcePack)
        mergeResourcePacks(resourcePack, overwritePack)
    }

    /**
     * Clears a ResourcePack-object for all files, similar to creating a new object
     */
    fun clearResourcePack(resourcePack: ResourcePack) {
        resourcePack.icon(null)
        resourcePack.metadata(Metadata.empty())
        resourcePack.models().map { it.key() }.forEach(resourcePack::removeModel)
        resourcePack.textures().map { it.key() }.forEach(resourcePack::removeTexture)
        resourcePack.atlases().map { it.key() }.forEach(resourcePack::removeAtlas)
        resourcePack.languages().map { it.key() }.forEach(resourcePack::removeLanguage)
        resourcePack.blockStates().map { it.key() }.forEach(resourcePack::removeBlockState)
        resourcePack.fonts().map { it.key() }.forEach(resourcePack::removeFont)
        resourcePack.sounds().map { it.key() }.forEach(resourcePack::removeSound)
        resourcePack.soundRegistries().map { it.namespace() }.forEach(resourcePack::removeSoundRegistry)
        resourcePack.unknownFiles().map { it.key }.forEach(resourcePack::removeUnknownFile)
        resourcePack.items().map { it.key() }.forEach(resourcePack::removeItem)
        resourcePack.equipment().map { it.key() }.forEach(resourcePack::removeEquipment)
    }

    /**
     * Merges the content of two ResourcePacks, handling conflicts where possible
     * Will sort ItemOverrides for models
     */
    fun mergeResourcePacks(resourcePack: ResourcePack, importedPack: ResourcePack) {
        (importedPack.packMeta() ?: resourcePack.packMeta())?.apply(resourcePack::packMeta)
        (importedPack.icon() ?: resourcePack.icon())?.apply(resourcePack::icon)

        resourcePack.overlays().plus(importedPack.overlays()).groupBy { it.directory() }.forEach { (directory, overlays) ->
            val newOverlay = Overlay.overlay(directory)
            overlays.forEach { overlay -> mergeContainers(newOverlay, overlay) }
            resourcePack.overlay(newOverlay)
        }

        (resourcePack.overlaysMeta()?.entries() ?: mutableListOf<OverlayEntry>())
            .plus(importedPack.overlaysMeta()?.entries() ?: mutableListOf<OverlayEntry>())
            .also { resourcePack.overlaysMeta(OverlaysMeta.of(it)) }

        (resourcePack.sodiumMeta()?.ignoredShaders() ?: mutableListOf<String>())
            .plus(importedPack.sodiumMeta()?.ignoredShaders() ?: mutableListOf<String>())
            .also { resourcePack.sodiumMeta(SodiumMeta.sodium(it)) }

        mergeContainers(resourcePack, importedPack)
    }

    private fun mergeContainers(container: ResourceContainer, importedContainer: ResourceContainer) {
        importedContainer.textures().forEach(container::texture)
        importedContainer.sounds().forEach(container::sound)
        importedContainer.unknownFiles().forEach(container::unknownFile)

        importedContainer.equipment().forEach { equipment ->
            val oldEquipment = container.equipment(equipment.key()) ?: return@forEach container.equipment(equipment)
            val layersByType = LinkedHashMap(oldEquipment.layers())
            equipment.layers().forEach { (type, layers) ->
                layersByType.compute(type) { _, oldLayers ->
                    return@compute (oldLayers ?: listOf()).plus(layers)
                }
            }

            container.equipment(oldEquipment.layers(layersByType))
        }

        importedContainer.items().forEach { item ->
            val oldItem = container.item(item.key()) ?: return@forEach container.item(item)

            fun mergeItemModels(oldItem: ItemModel, newItem: ItemModel): ItemModel {
                return when (newItem) {
                    is ReferenceItemModel -> ItemModel.reference(newItem.model(), newItem.tints().plus((oldItem as? ReferenceItemModel)?.tints() ?: listOf()))
                    is CompositeItemModel -> ItemModel.composite(newItem.models().plus((oldItem as? CompositeItemModel)?.models() ?: listOf()))
                    is SelectItemModel -> newItem.toBuilder().addCases((oldItem as? SelectItemModel)?.cases() ?: listOf()).build()
                    is RangeDispatchItemModel -> newItem.toBuilder().addEntries((oldItem as? RangeDispatchItemModel)?.entries() ?: listOf()).build()
                    is ConditionItemModel -> {
                        val oldCondition = (oldItem as? ConditionItemModel)?.takeIf { it.condition() == newItem.condition() }
                        val mergedTrue = oldCondition?.onTrue()?.let { mergeItemModels(it, newItem.onTrue()) } ?: newItem.onTrue()
                        val mergedFalse = oldCondition?.onFalse()?.let { mergeItemModels(it, newItem.onFalse()) } ?: newItem.onFalse()
                        ItemModel.conditional(newItem.condition(), mergedTrue, mergedFalse)
                    }
                    else -> item.model()
                }
            }

            when {
                oldItem.model() is ReferenceItemModel -> item.model()
                oldItem.model() is EmptyItemModel -> item.model()
                oldItem.model().javaClass == item.model().javaClass -> mergeItemModels(oldItem.model(), item.model())
                else -> {
                    idofrontLogger.e("Failed to merge ItemModels ${item.key().asString()}, ")
                    idofrontLogger.w("Existing ItemModel of incompatible type ${oldItem.model().javaClass.simpleName}, keeping old ItemModel...")
                    null
                }
            }?.let { Item.item(item.key(), it, item.handAnimationOnSwap()) }?.addTo(container)
        }

        importedContainer.models().forEach { model: Model ->
            model.toBuilder().apply {
                container.model(model.key())?.overrides()?.forEach(::addOverride)
            }.build().addTo(container)
        }

        importedContainer.fonts().forEach { font: Font ->
            font.toBuilder().apply {
                container.font(font.key())?.providers()?.forEach(::addProvider)
            }.build().addTo(container)
        }

        importedContainer.soundRegistries().forEach { soundRegistry: SoundRegistry ->
            val baseRegistry = container.soundRegistry(soundRegistry.namespace())
            if (baseRegistry != null) {
                val mergedEvents = LinkedHashSet(baseRegistry.sounds())
                mergedEvents.addAll(soundRegistry.sounds())
                SoundRegistry.soundRegistry(baseRegistry.namespace(), mergedEvents).addTo(container)
            } else soundRegistry.addTo(container)
        }

        importedContainer.atlases().forEach { atlas ->
            atlas.toBuilder().apply {
                container.atlas(atlas.key())?.sources()?.forEach(::addSource)
            }.build().addTo(container)
        }

        importedContainer.languages().forEach { language: Language ->
            container.language(language.key())?.let { base: Language ->
                base.translations().entries.forEach { (key, value) ->
                    language.translations().putIfAbsent(key, value)
                }
            }
            language.addTo(container)
        }

        importedContainer.blockStates().forEach { blockState: BlockState ->
            container.blockState(blockState.key())?.let { base: BlockState ->
                blockState.multipart().addAll(base.multipart())
                blockState.variants().putAll(base.variants())
            }
            blockState.addTo(container)
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
}