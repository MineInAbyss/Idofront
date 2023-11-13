package com.mineinabyss.idofront.features

import com.mineinabyss.idofront.commands.Command

abstract class Feature {
    open val dependsOn: Set<String> = setOf()

    internal val mainCommandExtras = mutableListOf<Command.() -> Unit>()
    internal val tabCompletions = mutableListOf<TabCompletion.() -> List<String>?>()

    protected open fun FeatureDSL.load() {}

    protected open fun FeatureDSL.enable() {}

    protected open fun FeatureDSL.disable() {}

    /** Runs before enable, if feature has a context, it should be created after [load] and before [enable] */
    @JvmName("loadFeature")
    fun load(context: FeatureDSL) {
        context.load()
    }

    @JvmName("enableFeature")
    fun enable(context: FeatureDSL) {
        context.enable()
    }

    @JvmName("disableFeature")
    fun disable(context: FeatureDSL) {
        mainCommandExtras.clear()
        context.disable()
    }

    fun tabCompletion(completion: TabCompletion.() -> List<String>?) {
        tabCompletions += completion
    }

    fun mainCommand(run: Command.() -> Unit) {
        mainCommandExtras += run
    }
}

