package com.mineinabyss.idofront.services

import org.bukkit.inventory.ItemStack

fun interface ItemProvider {
    /** Edits [applyTo] as needed based on the provided [definition] in config. */
    fun provide(applyTo: ItemStack, definition: String): Boolean
}

/**
 * Service used by SerializableItemStack to let other plugins provide a base item,
 * split by a space, ex. `mycustomplugin myitemdefinition`.
 *
 * ## Example usage
 *
 * ```kotlin
 * Bukkit.getServicesManager().load(SerializableItemStackService::class.java)?.registerProvider("myPrefix") { item, definition ->
 *     // Customize item as needed based on `definition`
 * }
 * ```
 */
interface SerializableItemStackService {
    /**
     * Registers a new item provider for a given [prefix].
     *
     * If a provider already exists for the prefix, the existing provider will be overwritten.
     *
     * @param [prefix] An identifier for this item type, must not contain spaces.
     */
    fun registerProvider(prefix: String, provider: ItemProvider)

    /**
     * Gets a registered item provider.
     */
    fun getProvider(prefix: String): ItemProvider?
}
