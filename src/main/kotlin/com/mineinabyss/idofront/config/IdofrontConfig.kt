package com.mineinabyss.idofront.config

import com.charleskorn.kaml.Yaml
import com.mineinabyss.idofront.messaging.logInfo
import com.mineinabyss.idofront.messaging.logSuccess
import kotlinx.serialization.KSerializer
import kotlinx.serialization.StringFormat
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
        val format: StringFormat = Yaml.default
) {
    init {
        logInfo("Registering configuration ${file.name}")
        if (!file.exists()) {
            file.parentFile.mkdirs()
            plugin.saveResource(file.name, false) //TODO work this out
            logSuccess("${file.name} has been created")
        }
        logSuccess("Registered configuration: ${file.name}")
    }

    var data: T = loadData()

    /** Saves the current serialized data */
    open fun save() = file.writeText(format.stringify(serializer, data))

    /** Discards current data and re-reads and serializes it */
    private fun loadData(): T = format.parse(serializer, file.readText()).also { data = it }

    /** Reloads this configuration file with information from the disk */
    fun reload(sender: CommandSender = plugin.server.consoleSender) {
        val context = ReloadScope(sender)
        context.apply {
            attempt("Loaded serialized config values", "Failed to load serialized config values") {
                loadData()
            }
        }
        reload().invoke(context)
    }

    /** Reload logic with useful methods from [ReloadScope] */
    protected open fun reload(): ReloadScope.() -> Unit = {}
}