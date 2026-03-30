package com.mineinabyss.idofront.features

import com.mineinabyss.idofront.commands.brigadier.IdoRootCommand
import com.mineinabyss.idofront.commands.brigadier.RootIdoCommands
import com.mineinabyss.idofront.commands.brigadier.commands
import org.bukkit.plugin.Plugin
import org.kodein.di.DI
import org.kodein.di.DirectDI
import kotlin.reflect.KClass

data class LoadPredicate(val reason: String, val predicate: DirectDI.() -> Boolean)

class FeatureBuilder(
    val name: String,
    val type: KClass<out Any>,
) : FeatureDSL {
    private var dependencies = FeatureDependencies(listOf(), listOf(), listOf())
    private var diBuilder: DI.Builder.() -> Unit = {}
    private var onEnable: MutableList<FeatureDI.() -> Unit> = mutableListOf()
    private val onLoad: MutableList<DirectDI.() -> Unit> = mutableListOf()
    private val subFeatures = mutableSetOf<Feature<*>>()

    fun dependencies(block: DI.Builder.() -> Unit) {
        diBuilder = block
    }

    fun onLoad(block: DirectDI.() -> Unit) {
        onLoad += block
    }

    class FeatureDependenciesBuilder {
        private val features = mutableListOf<Feature<*>>()
        private val plugins = mutableListOf<String>()
        private val conditions = mutableListOf<LoadPredicate>()

        fun features(vararg feature: Feature<*>) {
            features += feature
        }

        fun plugins(vararg plugins: String) {
            this.plugins += plugins
        }

        fun condition(
            reason: String = "Conditions not met",
            predicate: DirectDI.() -> Boolean,
        ) {
            conditions += LoadPredicate(reason, predicate)
        }

        fun build() = FeatureDependencies(
            features = features.toList(),
            plugins = plugins.toList(),
            conditions = conditions.toList(),
        )
    }

    fun dependsOn(block: FeatureDependenciesBuilder.() -> Unit) {
        dependencies = FeatureDependenciesBuilder().apply(block).build()
    }

    fun install(vararg features: Feature<*>) {
        subFeatures += features
    }


    //TODO disabling feature should close any AutoCloseable scoped entries
//    fun scopedModule(block: ScopeDSL.() -> Unit) {
//        scopedModule = block
//    }

    fun commands(block: context(DICommandContext) RootIdoCommands.() -> Unit) {
        onLoad {
            val context = DICommandContext(get(), get())
            get<Plugin>().commands {
                block(context, this)
            }
        }
    }

    fun mainCommand(block: context(DICommandContext) IdoRootCommand.() -> Unit) {
        onLoad {
            get<MainCommand>().subcommand(block)
        }
    }


    fun onEnable(block: FeatureDI.() -> Unit) {
        onEnable += block
    }

    fun <T : Any> build(extract: DirectDI.() -> T): Feature<T> {
        val onEnable = onEnable.toList()
        val onLoad = onLoad.toList()

        return Feature(
            name = name,
            type = type as KClass<T>,
            dependencies = dependencies,
            subFeatures = subFeatures.toSet(),
            diBuilder = {
                diBuilder()

                // Call onEnable when ready
                onReady {
                    val featureDI = object : DirectDI by this, FeatureDI {}
                    onEnable.forEach { it(featureDI) }
                }
            },
            extract = extract,
            onLoad = { onLoad.forEach { it() } },
        )
    }
}

