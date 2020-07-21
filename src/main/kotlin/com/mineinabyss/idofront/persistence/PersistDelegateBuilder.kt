package com.mineinabyss.idofront.persistence

import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin
import kotlin.reflect.KProperty

class PersistDelegateInfo(
        val persistentDataContainer: PersistentDataContainer,
        val plugin: Plugin,
        val keyPrefix: String? = null
)

class PersistDelegateBuilder<T>(
        val type: PersistentDataType<T, T>,
        var persistDelegateInfo: PersistDelegateInfo,
        var defaultValue: T? = null
) {
    operator fun provideDelegate(thisRef: Any, prop: KProperty<*>): PersistDelegate<T> {
        return PersistDelegate(
                prop,
                type,
                persistDelegateInfo,
                defaultValue
        )
    }
}

class PersistDelegate<T> internal constructor(
        val property: KProperty<*>,
        private val type: PersistentDataType<T, T>,
        private val persistInfo: PersistDelegateInfo,
        defaultValue: T?
) {
    private val name: String = property.name
    private val persistentDataContainer = persistInfo.persistentDataContainer

    init {
        try {
            getValue(null, property)
        } catch (e: IllegalStateException) {
            //set value if it isn't present
            if (defaultValue != null) setValue(null, property, defaultValue)
        }
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T =
            persistentDataContainer.get(key, type) ?: error("$key not found in $name")

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) =
            persistentDataContainer.set(key, type, value)

    //TODO should this be created right away or only when first accessed?
    private val key: NamespacedKey by lazy {
        //TODO dunno if key uses a dot in between
        val keyPrefix: String? = persistInfo.keyPrefix?.plus(".") ?: ""
        NamespacedKey(persistInfo.plugin, "$keyPrefix.$name")
    }
}