package com.mineinabyss.idofront.config

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.mineinabyss.idofront.messaging.logInfo
import com.mineinabyss.idofront.messaging.logSuccess
import kotlinx.serialization.KSerializer
import kotlinx.serialization.StringFormat
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin
import java.nio.file.Path
import kotlin.io.path.*

/**
 * Stores configuration data for your config files
 *
 * @param T The type of the serializable class that holds data for this config
 * @param plugin The plugin this config belongs to.
 * @param serializer [T]'s serializer
 * @param file Defaults to your `config.yml` inside [plugin]'s [data folder][Plugin.getDataFolder].
 * @param format The serialization format. Defaults to YAML.
 */
open class IdofrontConfig<T>(
    val plugin: Plugin,
    val serializer: KSerializer<T>,
    val file: Path = plugin.dataFolder.toPath() / "config.yml",
    val format: StringFormat = Yaml(configuration = YamlConfiguration(strictMode = false))
) {
    /** The deserialized data for this configuration. */
    var data: T = loadData()
        private set
    private var dirty = false

    init {
        logInfo("Registering configuration ${file.name}")
        if (!file.exists()) {
            file.parent.createDirectories()
            plugin.saveResource(file.name, false)
            logSuccess("${file.name} has been created")
        }
        logSuccess("Registered configuration: ${file.name}")
    }

    /** Marks this config as dirty and saves all changes made in 30 seconds */
    fun queueSave() {
        if (!dirty) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, Runnable {
                if (dirty) save()
            }, 600) //save changes in 30 seconds
            dirty = true
        }
    }

    //TODO make data dirty when any property is modified and save it every so often
    /** Saves the current serialized data immediately */
    open fun save() {
        dirty = false
        file.writeText(format.encodeToString(serializer, data))
    }

    /** Discards current data and re-reads and serializes it */
    private fun loadData(): T {
        dirty = false
        //TODO decode/encode from stream if an interface ever appears
        return format.decodeFromString(serializer, file.readText()).also { data = it }
    }

    /** Runs extra load logic for this configuration. */
    fun load(sender: CommandSender = plugin.server.consoleSender) {
        ReloadScope(sender).apply {
            load()
        }
    }

    /** Reloads this configuration file with information from the disk */
    fun reload(sender: CommandSender = plugin.server.consoleSender) {
        ReloadScope(sender).apply {
            unload()
            "Loading serialized config values" {
                loadData()
            }
            load()
        }
    }

    /** Unload logic with helper methods from [ReloadScope] */
    protected open fun ReloadScope.unload() {}

    /** Load logic with helper methods from [ReloadScope] */
    protected open fun ReloadScope.load() {}
}
