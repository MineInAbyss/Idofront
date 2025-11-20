package com.mineinabyss.idofront.config

import com.charleskorn.kaml.*
import com.mineinabyss.idofront.messaging.idofrontLogger
import java.nio.file.Path
import kotlin.io.path.*

class MultiEntryYamlReader<T>(
    val config: Config<T>,
    private val rootDir: Path,
    private val yamlFormat: Yaml,
) {
    @OptIn(ExperimentalPathApi::class)
    fun read(): List<ConfigEntryWithKey<T>> {
        val nodes = mutableMapOf<String, EntryWithNode<T>>()
        val entries = mutableListOf<ConfigEntryWithKey<T>>()
        rootDir.walk()
            .filter { it.isRegularFile() && it.extension == "yml" }
            .forEach { path ->
                val spawnYaml = yamlFormat.parseToYamlNode(path.inputStream()).yamlMap
                spawnYaml.entries.forEach { (name, entry) ->
                    val nameStr = name.content
                    if (nameStr == "namespaces") return@forEach
                    runCatching {
                        val decoded = decodeEntry(yamlFormat, nodes, entry)
                        entries += ConfigEntryWithKey(path, nameStr, decoded.entry)
                        nodes[nameStr] = decoded
                    }
                        .onSuccess {
                            idofrontLogger.d { "Read entry $nameStr entry from $path" }
                        }
                        .onFailure {
                            idofrontLogger.w { "Failed to read entry $nameStr entry from $path" }
                            idofrontLogger.w { it.localizedMessage }
                            idofrontLogger.d { it.stackTraceToString() }
                        }
                }

            }
        return entries
    }

    internal fun decodeEntry(
        yaml: Yaml,
        decodedEntries: Map<String, EntryWithNode<T>>,
        yamlNode: YamlNode,
    ): EntryWithNode<T> {
        val inherit = yamlNode.yamlMap.get<YamlNode>("inherit")
        val merged = if (inherit != null) {
            val inheritList = if (inherit is YamlList) inherit.items else listOf(inherit.yamlScalar)
            val inheritNodes = inheritList.mapNotNull { decodedEntries[it.yamlScalar.content]?.node }
            (inheritNodes + yamlNode).reduce(Companion::mergeYamlNodes)
        } else yamlNode
        return EntryWithNode(yaml.decodeFromYamlNode(config.serializer, merged), merged)
    }

    internal data class EntryWithNode<T>(
        val entry: T,
        val node: YamlNode,
    )

    companion object {
        private val specialMergeTags = Regex("(\\\$inherit)|(\\\$remove)")

        internal fun mergeYamlNodes(original: YamlNode?, override: YamlNode): YamlNode = when {
            original is YamlMap && override is YamlMap -> {
                val mapEntries =
                    original.entries.entries.associate { it.key.content to (it.key to it.value) }.toMutableMap()
                override.entries.forEach { (key, node) ->
//                    if (key.content in mapEntries) {
                    mapEntries[key.content] = key to mergeYamlNodes(mapEntries[key.content]?.second, node)
//                    } else mapEntries[key.content] = key to node
                }
                YamlMap(
                    mapEntries.values.toMap(),
                    original.path
                )
            }

            original is YamlList && override is YamlList -> {
                val inheritKey = override.items.firstOrNull { (it as? YamlScalar)?.content == "\$inherit" }
                val removeTags = override.items.flatMap {
                    (it as? YamlScalar)?.content?.takeIf { it.startsWith("\$remove") }?.removePrefix("\$remove")
                        ?.trim()
                        ?.split(' ')
                        ?: emptyList()
                }.toSet()

                if (inheritKey != null)
                    YamlList(
                        original.items
                            .filter { (it as? YamlMap)?.entries?.any { it.key.content in removeTags } != true }
                            .plus(override.items.filter {
                                (it as? YamlScalar)?.content?.contains(specialMergeTags) != true
                            }), override.path
                    )
                else override
            }

            // If original is not a list, but we ask to inherit, remove these tags from the override
            override is YamlList -> {
                YamlList(override.items.filter { (it as? YamlScalar)?.content?.contains(specialMergeTags) != true }, override.path)
            }

            else -> override
        }
    }
}