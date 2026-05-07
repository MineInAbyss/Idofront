package com.mineinabyss.idofront.features

import com.mineinabyss.dependencies.DI
import com.mineinabyss.dependencies.DIScope
import com.mineinabyss.dependencies.get
import com.mineinabyss.dependencies.getOrNull
import com.mineinabyss.idofront.commands.brigadier.IdoRootCommand
import com.mineinabyss.idofront.commands.brigadier.context.IdoCommandContext

data class MainCommand(
    val names: List<String>,
    val description: String?,
    val reloadCommandName: String? = null,
    val reloadCommandPermission: String? = null,
    val reloadableFeatures: List<DI.Module>? = null,
    val permission: String? = null,
    /** Runs before any individual feature reloads or all features reload. */
    val onBeforeReload: () -> Unit = {},
) {
    internal val subcommands = mutableListOf<Subcommand>()
    fun subcommand(module: DI.Module, block: context(DICommandContext) IdoRootCommand.() -> Unit) {
        subcommands += Subcommand(module, block)
    }

    data class Subcommand(
        val module: DI.Module,
        val create: context(DICommandContext) IdoRootCommand.() -> Unit,
    )
}

data class DICommandContext(val scope: DIScope, val module: DI.Module)


/**
 * Gets something from the [DI] context of the current instance of the feature this command block comes from.
 *
 * I.e. commands are only registered on server startup, but this function allows you to read up-to-date dependencies even after a feature reloads
 * (the limitation being it can only be used inside executes or Args blocks)
 */
context(di: DICommandContext)
inline fun <reified T : Any> IdoCommandContext.get(): T = di
    .scope[di.module]
    .getOrNull<T>()
    ?: error("Command tried to get feature config of an unloaded feature: ${di.module.name}.")
