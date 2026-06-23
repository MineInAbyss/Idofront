package com.mineinabyss.idofront.resourcepacks

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mineinabyss.idofront.Idofront
import com.mineinabyss.idofront.messaging.logger
import net.kyori.adventure.key.Key
import org.bukkit.Bukkit
import team.unnamed.creative.ResourcePack
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStreamReader
import java.net.URI
import java.util.concurrent.CompletableFuture
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream


object MinecraftAssetExtractor {

    private const val VERSION_MANIFEST_URL = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json"
    var resourcePack = ResourcePack.resourcePack()
    val vanillaSounds = mutableListOf<Key>()
    val zipPath = Bukkit.getPluginsFolder().resolve("Idofront/assetCache/${Bukkit.getMinecraftVersion()}.zip")
    private var future: CompletableFuture<Void>? = null

    fun extractLatest(): CompletableFuture<Void> {
        if (future == null || (!zipPath.exists() && future!!.isDone)) future = CompletableFuture.runAsync {
            val vanillaSoundsJson = zipPath.resolveSibling("vanilla-sounds.json")
            zipPath.parentFile.apply {
                mkdirs()
            }

            runCatching {
                if (vanillaSoundsJson.createNewFile() || vanillaSoundsJson.readText().isEmpty()) {
                    val versionInfo = downloadJson(findVersionInfoUrl())
                    extractVanillaSounds(assetIndex(versionInfo!!)!!)
                }

                JsonParser.parseString(vanillaSoundsJson.readText()).asJsonObject.getAsJsonArray("sounds").forEach { json: JsonElement ->
                    vanillaSounds += Key.key(json.asString)
                }
            }.onFailure { it.printStackTrace() }

            // Caches extracted before pack.mcmeta was kept parse under the oldest pack format,
            // which rejects anything newer, like element rotations outside [-45, 45]
            if (zipPath.exists() && !zipPath.containsPackMeta()) {
                Idofront.logger.i("Vanilla-resourcepack cache is missing its pack.mcmeta, re-extracting...")
                zipPath.delete()
            }

            if (zipPath.exists()) return@runAsync readVanillaRP()

            Idofront.logger.i("Extracting latest vanilla-resourcepack...")

            val versionInfo = runCatching {
                downloadJson(findVersionInfoUrl())
            }.onFailure {
                Idofront.logger.w("Failed to fetch version-info for vanilla-resourcepack...")
                it.printStackTrace()
                return@runAsync
            }.getOrNull() ?: return@runAsync

            val clientJar = downloadClientJar(versionInfo)
            extractJarAssetsToZip(clientJar!!, zipPath)

            val assetIndex = assetIndex(versionInfo)
            extractVanillaSounds(assetIndex!!)

            readVanillaRP()
            Idofront.logger.s("Finished extracting latest vanilla-resourcepack!")
        }

        return future!!
    }

    /**
     * The client jar ships no pack.mcmeta, so creative would read its assets as the oldest pack format
     * and reject anything newer, like element rotations outside [-45, 45]
     */
    private fun writePackMeta(zos: ZipOutputStream) {
        val format = serverPackFormat ?: return Idofront.logger.w("Could not read the server's resourcepack-format, vanilla assets may fail to parse")

        zos.putNextEntry(ZipEntry("pack.mcmeta"))
        zos.write("""{"pack":{"pack_format":$format,"description":"Vanilla assets"}}""".toByteArray())
        zos.closeEntry()
    }

    /** Bukkit does not expose the resourcepack-format, but the vanilla constant it comes from is stable */
    private val serverPackFormat: Int? by lazy {
        runCatching {
            Class.forName("net.minecraft.SharedConstants").getField("RESOURCE_PACK_FORMAT_MAJOR").getInt(null)
        }.getOrNull()
    }

    private fun File.containsPackMeta() = runCatching {
        java.util.zip.ZipFile(this).use { it.getEntry("pack.mcmeta") != null }
    }.getOrDefault(false)

    private fun readVanillaRP() {
        runCatching {
            resourcePack = ResourcePacks.resourcePackReader.readFromZipFile(zipPath)
        }.onFailure {
            Idofront.logger.w("Failed to read Vanilla ResourcePack-cache...")
            it.printStackTrace()
        }
    }

    private fun extractVanillaSounds(assetIndex: JsonObject) {
        val objects = assetIndex.getAsJsonObject("objects")
        val sounds = JsonArray()

        objects.keySet().forEach { key ->
            val soundKey = Key.key(key.replace("minecraft/sounds/", "").replace(".ogg", ""))
            if (!key.startsWith("minecraft/sounds/")) return@forEach

            vanillaSounds += soundKey
            sounds.add(soundKey.asString())
        }

        zipPath.resolveSibling("vanilla-sounds.json").writeText(JsonObject().apply { add("sounds", sounds) }.toString())
    }

    private fun assetIndex(versionInfo: JsonObject): JsonObject? {
        val assetIndex = versionInfo.getAsJsonObject("assetIndex")
        val url = assetIndex["url"].asString

        return runCatching {
            downloadJson(url)!!.asJsonObject
        }.onFailure {
            Idofront.logger.e("Failed to download asset index")
            it.printStackTrace()
        }.getOrNull()
    }

    private fun extractJarAssetsToZip(clientJar: ByteArray, zipFile: File) {
        runCatching {
            ByteArrayInputStream(clientJar).use { stream ->
                ZipInputStream(stream).use { zis ->
                    zipFile.outputStream().use { fos ->
                        ZipOutputStream(fos).use { zos ->
                            // Has to come first, creative parses every file it reads before this with an unknown format
                            writePackMeta(zos)

                            var entry = zis.nextEntry
                            while (entry != null) {
                                val name = entry.name
                                if (name.startsWith("assets/")
                                    && !name.endsWith("scaffolding_unstable.json")
                                    && !name.startsWith("assets/minecraft/shaders")
                                    && !name.startsWith("assets/minecraft/particles")
                                ) {
                                    // Prepare the new ZipEntry
                                    val zipEntry = ZipEntry(name)
                                    zos.putNextEntry(zipEntry)

                                    // Write file content to the zip
                                    zis.copyTo(zos)
                                    zos.closeEntry()
                                }
                                zis.closeEntry()
                                entry = zis.nextEntry
                            }
                        }
                    }
                }
            }
        }.onFailure {
            Idofront.logger.w("Failed to extract vanilla-resourcepack directly to zip file...")
            it.printStackTrace()
        }
    }

    private fun downloadClientJar(versionInfo: JsonObject): ByteArray? {
        val url = versionInfo.getAsJsonObject("downloads").getAsJsonObject("client")["url"].asString
        return runCatching {
            URI(url).toURL().openStream().use { stream ->
                stream.readAllBytes()
            }
        }.onFailure {
            Idofront.logger.w("Failed to download vanilla-resourcepack from: $url")
            it.printStackTrace()
        }.getOrNull()
    }

    private fun findVersionInfoUrl(): String? {
        val manifest = downloadJson(VERSION_MANIFEST_URL) ?: return null

        return manifest.getAsJsonArray("versions").firstOrNull { element ->
            element.asJsonObject?.get("id")?.asString == Bukkit.getMinecraftVersion()
        }?.asJsonObject?.get("url")?.asString
    }

    private fun downloadJson(url: String?): JsonObject? {
        if (url == null) return null
        return runCatching {
            URI.create(url).toURL().openStream().use { stream ->
                InputStreamReader(stream).use { reader ->
                    JsonParser.parseReader(reader).asJsonObject
                }
            }
        }.onFailure {
            Idofront.logger.w("Failed to fetch manifest for vanilla-resourcepack...")
            it.printStackTrace()
        }.getOrNull()
    }
}
