package com.mineinabyss.idofront.serialization.recipes

import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.set.RegistrySet
import io.papermc.paper.registry.tag.TagKey
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.inventory.ItemType
import org.bukkit.inventory.RecipeChoice

object RecipeUtils {
    fun itemTypeChoiceForTag(key: NamespacedKey): RecipeChoice {
        val materials = mutableListOf<ItemType>()
        Registry.ITEM.get(key)?.let(materials::add)
        materials += Registry.ITEM.getTagValues(TagKey.create(RegistryKey.ITEM, key))

        if (materials.isEmpty()) return RecipeChoice.empty()
        return RecipeChoice.itemType(RegistrySet.keySetFromValues(RegistryKey.ITEM, materials))
    }
}
