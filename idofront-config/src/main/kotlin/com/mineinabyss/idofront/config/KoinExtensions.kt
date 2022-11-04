package com.mineinabyss.idofront.config

import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.core.qualifier.named

/**
 * Adds this config's data type to the module, data will be updated if the config is reloaded and using `inject`.
 */
inline fun <reified T> Module.singleConfig(config: IdofrontConfig<T>) {
    single(named<T>()) { config }
    // config never changes, but we register a factory to access the latest data object, in case it is updated
    factory { config.data }
}

inline fun <reified T> KoinComponent.injectConfig() = inject<IdofrontConfig<T>>(named<T>())
inline fun <reified T> KoinComponent.getConfig() = get<IdofrontConfig<T>>(named<T>())
