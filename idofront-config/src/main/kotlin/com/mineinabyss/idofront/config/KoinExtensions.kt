package com.mineinabyss.idofront.config

import org.koin.core.module.Module

/**
 * Adds this config's data type to the module, data will be updated if the config is reloaded and using `inject`.
 */
inline fun <reified T> Module.singleConfig(config: IdofrontConfig<T>) {
    // config never changes, but we register a factory to access the latest data object, in case it is updated
    factory { config.data }
}
