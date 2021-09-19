package com.mineinabyss.idofront.nms.typeinjection

import com.mineinabyss.idofront.messaging.logWarn
import com.mineinabyss.idofront.nms.aliases.NMSEntity
import com.mineinabyss.idofront.nms.aliases.NMSEntityType
import com.mojang.datafixers.DataFixUtils
import com.mojang.datafixers.types.Type
import net.minecraft.SharedConstants
import net.minecraft.core.IRegistry
import net.minecraft.resources.MinecraftKey
import net.minecraft.util.datafix.fixes.DataConverterTypes
import org.bukkit.NamespacedKey

typealias NMSRegistry<T> = IRegistry<T>
typealias NMSNamespacedKey = MinecraftKey

object NMSRegsitryWrapper {
    val ENTITY_TYPE = NMSRegistry.Y
}

/**
 * Registers an [NMSEntityType] with the server.
 */
fun <T : NMSEntity> NMSEntityType<T>.registerEntityType(namespace: String, key: String): NMSEntityType<T> =
    NMSRegistry.a(NMSRegsitryWrapper.ENTITY_TYPE, NMSNamespacedKey(namespace, key), this)

/**
 * Injects an entity into the server
 *
 * Originally from [paper forums](https://papermc.io/forums/t/register-and-spawn-a-custom-entity-on-1-13-x/293)
 */
fun NMSEntityTypeBuilder.injectType(
    namespace: String,
    key: String,
    extendFrom: String
): NMSEntityType<NMSEntity> { //from https://papermc.io/forums/t/register-and-spawn-a-custom-entity-on-1-13-x/293
    @Suppress("UNCHECKED_CAST") val dataTypes = NMSDataConverterRegistry.getDataFixer()
        .getSchema(DataFixUtils.makeKey(SharedConstants.getGameVersion().worldVersion))
        .findChoiceType(NMSDataConverterTypesWrapper.ENTITY).types() as MutableMap<String, Type<*>>
    if (dataTypes.containsKey("$namespace:$key")) logWarn("ALREADY CONTAINS KEY: $key")
    dataTypes["$namespace:$key"] = dataTypes[extendFrom]!!

    return build("$namespace:$key").registerEntityType(namespace, key)
}

object NMSDataConverterTypesWrapper {
    val ENTITY = DataConverterTypes.q
}
