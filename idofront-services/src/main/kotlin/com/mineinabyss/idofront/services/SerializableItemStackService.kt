package com.mineinabyss.idofront.services

import org.bukkit.inventory.ItemStack

typealias ItemProvider = (applyTo: ItemStack, definition: String) -> Boolean

/**
 * Service used by SerializableItemStack to let other plugins provide a base item,
 * split by a space, ex. `mycustomplugin myitemdefinition`.
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
