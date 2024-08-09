package com.mineinabyss.idofront.resourcepacks

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mineinabyss.idofront.messaging.idofrontLogger
import org.bukkit.Bukkit
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


object MinecraftAssetExtractor {

    private const val VERSION_MANIFEST_URL = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json"
    val assetPath = Bukkit.getPluginsFolder().resolve("Idofront/assetCache/${Bukkit.getMinecraftVersion()}")

    fun extractLatest() {
        if (assetPath.exists() && !assetPath.listFiles().isNullOrEmpty()) return
        idofrontLogger.i("Extracting latest vanilla-assets...")
        val versionInfo = runCatching {
            downloadJson(findVersionInfoUrl() ?: return)?.asJsonObject
        }.getOrNull() ?: return

        val clientJar = downloadClientJar(versionInfo)
        extractJarAssets(clientJar, assetPath)
        idofrontLogger.s("Finished extracting vanilla assets for ${assetPath.name}")
    }

    private fun extractJarAssets(clientJar: ByteArray?, assetPath: File) {
        var entry: ZipEntry

        ByteArrayInputStream(clientJar).use { inputStream ->
            ZipInputStream(inputStream).use { zipInputStream ->

                kotlin.runCatching {
                    while (zipInputStream.nextEntry.also { entry = it } != null) {
                        if (!entry.name.startsWith("assets/")) continue
                        if (entry.name.startsWith("assets/minecraft/shaders")) continue
                        if (entry.name.startsWith("assets/minecraft/particles")) continue

                        val file = checkAndCreateFile(assetPath, entry)
                        if (entry.isDirectory && !file.isDirectory && !file.mkdirs())
                            error("Failed to create directory ${entry.name}")
                        else {
                            val parent = file.parentFile
                            if (!parent.isDirectory && !parent.mkdirs()) error("Failed to create directory ${parent?.path}")

                            runCatching {
                                zipInputStream.copyTo(FileOutputStream(file))
                            }.onFailure {
                                error("Failed to extract ${entry.name} from ${parent?.path}")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun checkAndCreateFile(assetPath: File, entry: ZipEntry): File {
        val destFile = assetPath.resolve(entry.name)
        val dirPath = assetPath.canonicalPath
        val filePath = destFile.canonicalPath

        if (!filePath.startsWith(dirPath + File.separator)) error("Entry outside target: ${entry.name}")
        return destFile
    }

    private fun downloadClientJar(versionInfo: JsonObject) = runCatching {
        val url = versionInfo.getAsJsonObject("downloads").getAsJsonObject("client").get("url").asString
        URI(url).toURL().readBytes()
    }.onFailure { it.printStackTrace() }.getOrNull() ?: error("Failed to download client JAR")

    private fun findVersionInfoUrl(): String? {
        val version = Bukkit.getMinecraftVersion()
        if (!assetPath.mkdirs()) {
            idofrontLogger.i("Latest has already been extracted for $version, skipping...")
            return null
        }

        val manifest = runCatching {
            downloadJson(VERSION_MANIFEST_URL)
        }.getOrNull() ?: error("Failed to download version manifest")

        return manifest.getAsJsonArray("versions").firstOrNull {
            (it as? JsonObject)?.get("id")?.asString?.equals(version) ?: false
        }?.asJsonObject?.get("url")?.asString ?: error("Failed to find version inof url for version $version")
    }

    private fun downloadJson(url: String) = runCatching { JsonParser.parseString(URI.create(url).toURL().readText()) }
        .getOrNull()?.takeIf { it.isJsonObject }?.asJsonObject
}
