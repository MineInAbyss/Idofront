package com.mineinabyss.idofront.nms.typeinjection

import com.mineinabyss.idofront.messaging.logWarn
import com.mineinabyss.idofront.nms.aliases.NMSEntity
import com.mineinabyss.idofront.nms.aliases.NMSEntityType
import com.mojang.datafixers.DataFixUtils
import com.mojang.datafixers.types.Type
import net.minecraft.SharedConstants
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.datafix.DataFixers
import net.minecraft.util.datafix.fixes.References
import net.minecraft.world.entity.EntityType

/**
 * Registers an [NMSEntityType] with the server.
 */
fun <T : NMSEntity> NMSEntityType<T>.registerEntityType(namespace: String, key: String): NMSEntityType<T> =
    Registry.register(Registry.ENTITY_TYPE, ResourceLocation(namespace, key), this)

/**
 * Injects an entity into the server
 *
 * Originally from [paper forums](https://papermc.io/forums/t/register-and-spawn-a-custom-entity-on-1-13-x/293)
 */
fun EntityType.Builder<*>.injectType(
    namespace: String,
    key: String,
    extendFrom: String
): NMSEntityType<out NMSEntity> { //from https://papermc.io/forums/t/register-and-spawn-a-custom-entity-on-1-13-x/293
    @Suppress("UNCHECKED_CAST") val dataTypes = DataFixers.getDataFixer()
        .getSchema(DataFixUtils.makeKey(SharedConstants.getCurrentVersion().worldVersion))
        .findChoiceType(References.ENTITY).types() as MutableMap<String, Type<*>>
    if (dataTypes.containsKey("$namespace:$key")) logWarn("ALREADY CONTAINS KEY: $key")
    dataTypes["$namespace:$key"] = dataTypes[extendFrom]!!

    return build("$namespace:$key").registerEntityType(namespace, key)
}
