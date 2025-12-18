package com.mineinabyss.idofront.features

import org.bukkit.plugin.Plugin
import org.koin.core.Koin
import org.koin.core.module.Module

class FeatureManagerBuilder(val plugin: Plugin) {
    private var globalModule: Module.() -> Unit = {}
    private var installedFeatures = mutableListOf<Feature<*>>()
    private var mainCommand: MainCommand? = null

    fun globalModule(block: Module.() -> Unit) {
        globalModule = block
    }

    fun withMainCommand(
        vararg names: String,
        description: String? = null,
        permission: String? = null,
    ) {
        mainCommand = MainCommand(
            names = names.toList(),
            description = description,
            reloadCommandName = null,
            permission = permission
        )
    }

    fun withReloadSubcommand(
        name: String = "reload",
        permission: String? = null,
    ) {
        mainCommand = mainCommand?.copy(
            reloadCommandName = name,
            reloadCommandPermission = permission,
        )
    }

    fun install(vararg features: Feature<*>) {
        installedFeatures += features
    }

    fun build(koin: Koin): FeatureManager = FeatureManager(
        plugin = plugin,
        koin = koin,
        mainCommand = mainCommand,
        globalModule = globalModule,
        installed = installedFeatures.toList()
    )
}