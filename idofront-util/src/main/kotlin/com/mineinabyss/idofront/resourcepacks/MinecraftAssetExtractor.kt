package com.mineinabyss.idofront.resourcepacks

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mineinabyss.idofront.messaging.idofrontLogger
import org.bukkit.Bukkit
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.net.URI
import java.util.concurrent.CompletableFuture
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import net.kyori.adventure.key.Key
import team.unnamed.creative.ResourcePack
import kotlin.collections.forEach


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

            if (zipPath.exists()) return@runAsync readVanillaRP()

            idofrontLogger.i("Extracting latest vanilla-resourcepack...")

            val versionInfo = runCatching {
                downloadJson(findVersionInfoUrl())
            }.onFailure {
                idofrontLogger.w("Failed to fetch version-info for vanilla-resourcepack...")
                it.printStackTrace()
                return@runAsync
            }.getOrNull() ?: return@runAsync

            val clientJar = downloadClientJar(versionInfo)
            extractJarAssetsToZip(clientJar!!, zipPath)

            val assetIndex = assetIndex(versionInfo)
            extractVanillaSounds(assetIndex!!)

            readVanillaRP()
            idofrontLogger.s("Finished extracting latest vanilla-resourcepack!")
        }

        return future!!
    }

    private fun readVanillaRP() {
        runCatching {
            resourcePack = ResourcePacks.resourcePackReader.readFromZipFile(zipPath)
        }.onFailure {
            idofrontLogger.w("Failed to read Vanilla ResourcePack-cache...")
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
            idofrontLogger.e("Failed to download asset index")
            it.printStackTrace()
        }.getOrNull()
    }

    private fun extractJarAssetsToZip(clientJar: ByteArray, zipFile: File) {
        runCatching {
            ByteArrayInputStream(clientJar).use { stream ->
                ZipInputStream(stream).use { zis ->
                    zipFile.outputStream().use { fos ->
                        ZipOutputStream(fos).use { zos ->
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
            idofrontLogger.w("Failed to extract vanilla-resourcepack directly to zip file...")
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
            idofrontLogger.w("Failed to download vanilla-resourcepack from: $url")
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
            idofrontLogger.w("Failed to fetch manifest for vanilla-resourcepack...")
            it.printStackTrace()
        }.getOrNull()
    }
}
