package com.mineinabyss.idofront.resourcepacks

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.Strictness
import com.google.gson.stream.JsonReader
import com.mineinabyss.idofront.Idofront
import com.mineinabyss.idofront.messaging.logger
import net.kyori.adventure.key.Key
import team.unnamed.creative.ResourcePack
import team.unnamed.creative.base.Writable
import team.unnamed.creative.metadata.overlays.OverlaysMeta
import team.unnamed.creative.metadata.pack.PackFormat
import team.unnamed.creative.metadata.pack.PackMeta
import team.unnamed.creative.model.Model
import team.unnamed.creative.overlay.Overlay
import team.unnamed.creative.overlay.ResourceContainer
import team.unnamed.creative.part.ResourcePackPart
import team.unnamed.creative.serialize.minecraft.GsonUtil
import team.unnamed.creative.serialize.minecraft.MinecraftResourcePackReader
import team.unnamed.creative.serialize.minecraft.ResourceCategories
import team.unnamed.creative.serialize.minecraft.equipment.EquipmentSerializer
import team.unnamed.creative.serialize.minecraft.fs.FileTreeReader
import team.unnamed.creative.serialize.minecraft.io.BinaryResourceDeserializer
import team.unnamed.creative.serialize.minecraft.io.JsonResourceDeserializer
import team.unnamed.creative.serialize.minecraft.io.ResourceDeserializer
import team.unnamed.creative.serialize.minecraft.metadata.MetadataSerializer
import team.unnamed.creative.serialize.minecraft.model.ModelSerializer
import team.unnamed.creative.serialize.minecraft.sound.SoundRegistrySerializer
import team.unnamed.creative.texture.Texture
import team.unnamed.creative.util.Keys

/**
 * Ported from Nexo's NexoPackReader, itself based on creative's MinecraftResourcePackReaderImpl (MIT).
 * Every file is read on its own, so one that fails is logged and skipped,
 * where creative's reader throws on the first one and the whole pack is lost with it
 */
object LenientResourcePackReader : MinecraftResourcePackReader {
    private const val ASSETS_FOLDER = "assets"
    private const val TEXTURES_FOLDER = "textures"
    private const val EQUIPMENT_FOLDER = "equipment"
    private const val PACK_METADATA_FILE = "pack.mcmeta"
    private const val PACK_ICON_FILE = "pack.png"
    private const val SOUNDS_FILE = "sounds.json"
    private const val METADATA_EXTENSION = ".mcmeta"
    private const val OBJECT_EXTENSION = ".json"
    private val IGNORED_EXTENSIONS = listOf(".DS_Store", ".db", ".txt")

    override fun read(reader: FileTreeReader): ResourcePack = PackRead(reader).readAll()

    private class PackRead(private val reader: FileTreeReader) {
        private val resourcePack = ResourcePack.resourcePack()
        private var packFormat = PackFormat.UNKNOWN
        private var categories = ResourceCategories.buildCategoryMapByFolder(packFormat)
        private val packFormatsByOverlay = mutableMapOf<String, PackFormat>()

        // Textures and their .mcmeta arrive in any order, so whichever comes first waits here for the other.
        // A null overlay is the root pack
        private val incompleteTextures = mutableMapOf<String?, MutableMap<Key, Texture>>()

        fun readAll(): ResourcePack {
            while (reader.hasNext()) {
                val path = reader.next()
                runCatching { readFile(path) }.onFailure { Idofront.logger.w { "Skipped $path: ${it.message}" } }
            }
            incompleteTextures.forEach { (overlay, textures) ->
                val container = overlay?.let(resourcePack::overlay) ?: resourcePack
                textures.values.filter { it.data() !== Writable.EMPTY }.forEach(container::texture)
            }
            return resourcePack
        }

        private fun readFile(path: String) {
            val tokens = ArrayDeque(path.split("/").dropLastWhile(String::isEmpty))
            check(tokens.isNotEmpty()) { "Empty path" }
            if (tokens.size == 1) return readRootFile(path, tokens.first())

            var container: ResourceContainer = resourcePack
            var containerPath = path
            var overlay: String? = null
            var localPackFormat = packFormat
            var folder = tokens.removeFirst()

            if (folder in packFormatsByOverlay || folder != ASSETS_FOLDER) {
                // A file directly inside an overlay folder is not part of the pack
                if (tokens.isEmpty()) return resourcePack.unknownFile(path, reader.content().asWritable())
                overlay = folder
                container = resourcePack.overlay(folder) ?: Overlay.overlay(folder).also(resourcePack::overlay)
                containerPath = path.removePrefix("$folder/")
                localPackFormat = packFormatsByOverlay[folder] ?: PackFormat.UNKNOWN
                folder = tokens.removeFirst()
            }

            val unknown = { container.unknownFile(containerPath, reader.content().asWritable()) }
            if (folder != ASSETS_FOLDER || tokens.isEmpty()) return unknown()

            val namespace = tokens.removeFirst()
            if (!Keys.isValidNamespace(namespace) || tokens.isEmpty()) return unknown()

            val category = tokens.removeFirst()
            if (tokens.isEmpty()) {
                // sounds.json is the only file that sits directly in a namespace
                if (category != SOUNDS_FILE) return unknown()
                return container.soundRegistry(SoundRegistrySerializer.INSTANCE.readFromTree(parseJson(), namespace))
            }

            val categoryPath = tokens.joinToString("/")
            when {
                IGNORED_EXTENSIONS.any(categoryPath::endsWith) -> unknown()
                category == TEXTURES_FOLDER -> readTexture(container, overlay, namespace, categoryPath)
                else -> readResource(container, path, namespace, category, categoryPath, localPackFormat, unknown)
            }
        }

        private fun readRootFile(path: String, name: String) = when (name) {
            PACK_METADATA_FILE -> readMetadata(path)
            PACK_ICON_FILE -> resourcePack.icon(reader.content().asWritable())
            else -> resourcePack.unknownFile(path, reader.content().asWritable())
        }

        private fun readMetadata(path: String) {
            val metadata = MetadataSerializer.INSTANCE.readFromTree(parseJson())
            resourcePack.metadata(metadata)

            val formats = metadata.meta(PackMeta::class.java)?.formats()
            if (formats != null) {
                packFormat = formats
                categories = ResourceCategories.buildCategoryMapByFolder(formats)
            } else Idofront.logger.w { "$path has no pack meta, its pack format is unknown" }

            metadata.meta(OverlaysMeta::class.java)?.entries()?.forEach { packFormatsByOverlay[it.directory()] = it.formats() }
        }

        private fun readTexture(container: ResourceContainer, overlay: String?, namespace: String, categoryPath: String) {
            val incomplete = incompleteTextures.getOrPut(overlay) { mutableMapOf() }
            val metadataOf = categoryPath.withoutExtension(METADATA_EXTENSION)

            if (metadataOf != null) {
                val key = Key.key(namespace, metadataOf)
                val metadata = MetadataSerializer.INSTANCE.readFromTree(parseJson())
                val texture = incomplete.remove(key)
                if (texture != null) container.texture(texture.meta(metadata))
                else incomplete[key] = Texture.texture(key, Writable.EMPTY, metadata)
            } else {
                val key = Key.key(namespace, categoryPath)
                val data = reader.content().asWritable()
                val metadata = incomplete.remove(key)?.meta()
                if (metadata != null) container.texture(Texture.texture(key, data, metadata))
                else incomplete[key] = Texture.texture(key, data)
            }
        }

        private fun readResource(
            container: ResourceContainer,
            path: String,
            namespace: String,
            category: String,
            categoryPath: String,
            localPackFormat: PackFormat,
            unknown: () -> Unit,
        ) {
            val resourceCategory = categories[category]
            if (resourceCategory == null) {
                // A pack without a pack.mcmeta reads as an unknown format, where the equipment category
                // still maps to its pre-1.21.5 models/equipment folder, so the modern layout is parsed anyway
                val equipment = categoryPath.withoutExtension(OBJECT_EXTENSION)?.takeIf { category == EQUIPMENT_FOLDER }
                    ?: return unknown()
                return EquipmentSerializer.INSTANCE
                    .deserializeFromJson(parseJson(), Key.key(namespace, equipment), localPackFormat)
                    .addTo(container)
            }

            val value = categoryPath.withoutExtension(resourceCategory.extension(PackFormat.UNKNOWN)) ?: return unknown()
            val isEquipmentModel = "/models/equipment/" in path && resourceCategory.deserializer() is ModelSerializer
            val key = Key.key(namespace, if (isEquipmentModel) value.removePrefix("equipment/") else value)
            val deserializer: ResourceDeserializer<out ResourcePackPart> =
                if (isEquipmentModel) EquipmentSerializer.INSTANCE else resourceCategory.deserializer()

            val resource = when (deserializer) {
                is BinaryResourceDeserializer<out ResourcePackPart> -> deserializer.deserializeBinary(reader.content().asWritable(), key)
                is JsonResourceDeserializer<out ResourcePackPart> -> {
                    val json = parseJson()
                    deserializer.deserializeFromJson(json, key, localPackFormat).let { if (it is Model) it.withLayerGapsFixed(json) else it }
                }
                else -> deserializer.deserialize(reader.stream(), key)
            }
            resource.addTo(container)
        }

        private fun parseJson(): JsonElement = JsonReader(reader.stream().reader()).use {
            it.strictness = Strictness.LENIENT
            GsonUtil.parseReader(it)
        }
    }

    /**
     * Creative's [ModelSerializer] reads layers into an index-less list, so a model that only defines
     * e.g. `layer1` and relies on its parent for `layer0` is written back as `layer0`, overriding the parent.
     * A layer set that does not start at 0 or has gaps is moved into the variables, which keep their key
     */
    private fun Model.withLayerGapsFixed(json: JsonElement): Model {
        val textureKeys = (json as? JsonObject)?.getAsJsonObject("textures")?.keySet() ?: return this
        val layerIndices = textureKeys.filter { it.startsWith("layer") }.mapNotNull { it.removePrefix("layer").toIntOrNull() }
        if (layerIndices.isEmpty() || layerIndices == layerIndices.indices.toList()) return this

        // Creative's layer list keeps the json order, so positions line up with the indices
        val layers = textures().layers().takeUnless { it.size != layerIndices.size } ?: return this
        val textures = textures().toBuilder().layers(emptyList())
        layerIndices.forEachIndexed { position, index -> textures.addVariable("layer$index", layers[position]) }
        return toBuilder().textures(textures.build()).build()
    }

    private fun String.withoutExtension(extension: String): String? = takeIf { it.endsWith(extension) }?.removeSuffix(extension)
}
