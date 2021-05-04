package com.mineinabyss.idofront.config

import com.charleskorn.kaml.Yaml
import com.mineinabyss.idofront.messaging.logInfo
import com.mineinabyss.idofront.messaging.logSuccess
import kotlinx.serialization.KSerializer
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin
import java.io.File

/**
 * Stores configuration data for your config files
 *
 * @param T The type of the serializable class that holds data for this config
 * @param plugin The plugin this config belongs to.
 * @param serializer [T]'s serializer
 * @param file Defaults to your `config.yml` inside [plugin]'s [data folder][Plugin.getDataFolder].
 * @param format The serialization format. Defaults to YAML.
 */
abstract class IdofrontConfig<T>(
        val plugin: Plugin,
        val serializer: KSerializer<T>,
        val file: File = File(plugin.dataFolder, "config.yml"),
        val format: Yaml = Yaml.default
        //TODO switch to a superclass of Yaml & Json formats once no experimental opt-in annotation is needed
) {
    var data: T = loadData()
        private set
    private var dirty = false

    init {
        logInfo("Registering configuration ${file.name}")
        if (!file.exists()) {
            file.parentFile.mkdirs()
            plugin.saveResource(file.name, false) //TODO work this out
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
        return format.decodeFromString(serializer, file.readText()).also { data = it }
    }

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

    /** Reload logic with useful methods from [ReloadScope] */
    protected open fun ReloadScope.unload() {}

    /** Reload logic with useful methods from [ReloadScope] */
    protected open fun ReloadScope.load() {}

}
