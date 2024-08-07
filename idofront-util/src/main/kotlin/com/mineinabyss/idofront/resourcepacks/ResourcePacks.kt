package com.mineinabyss.idofront.resourcepacks

import com.mineinabyss.idofront.messaging.idofrontLogger
import team.unnamed.creative.ResourcePack
import team.unnamed.creative.serialize.minecraft.MinecraftResourcePackReader
import team.unnamed.creative.serialize.minecraft.MinecraftResourcePackWriter
import team.unnamed.creative.sound.SoundRegistry
import java.io.File

object ResourcePacks {
    val resourcePackWriter = MinecraftResourcePackWriter.builder().prettyPrinting(true).build()
    val resourcePackReader = MinecraftResourcePackReader.builder().lenient(true).build()

    fun readToResourcePack(file: File): ResourcePack? {
        return runCatching {
            when {
                !file.exists() -> null
                file.isDirectory && !file.listFiles().isNullOrEmpty() -> resourcePackReader.readFromDirectory(file)
                file.extension == "zip" -> resourcePackReader.readFromZipFile(file)
                else -> null
            }
        }.onFailure { idofrontLogger.w(file.name + ": " + it.message) }.getOrNull()
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

    fun mergeResourcePacks(originalPack: ResourcePack, mergePack: ResourcePack) {
        mergePack.textures().forEach(originalPack::texture)
        mergePack.sounds().forEach(originalPack::sound)
        mergePack.unknownFiles().forEach(originalPack::unknownFile)

        mergePack.models().forEach { model ->
            val baseModel = originalPack.model(model.key()) ?: return@forEach originalPack.model(model)
            originalPack.model(model.apply { overrides().addAll(baseModel.overrides()) })
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
        if (originalPack.icon() == null) mergePack.icon()?.let { originalPack.icon(it) }
        sortItemOverrides(originalPack)
    }

    /**
     * Ensures that the ResourcePack's models all have their ItemOverrides sorted based on their CustomModelData
     */
    fun sortItemOverrides(resourcePack: ResourcePack) {
        resourcePack.models().toHashSet().forEach { model ->
            val sortedOverrides = model.overrides().sortedBy { override ->
                // value() is a LazilyParsedNumber so convert it to an Int
                override.predicate().find { it.name() == "custom_model_data" }?.value()?.toString()?.toIntOrNull() ?: 0
            }
            resourcePack.model(model.toBuilder().overrides(sortedOverrides).build())
        }
    }
}