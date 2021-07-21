package com.mineinabyss.idofront.nms.typeinjection

import com.mineinabyss.idofront.messaging.logWarn
import com.mineinabyss.idofront.nms.aliases.NMSEntity
import com.mineinabyss.idofront.nms.aliases.NMSEntityType
import com.mojang.datafixers.DataFixUtils
import com.mojang.datafixers.types.Type
import net.minecraft.SharedConstants
import net.minecraft.core.IRegistry
import net.minecraft.util.datafix.fixes.DataConverterTypes

typealias NMSRegistry<T> = IRegistry<T>

object NMSRegsitryWrapper {
    val ENTITY_TYPE = NMSRegistry.Y
}

/**
 * Registers an [NMSEntityType] with the server.
 */
fun <T : NMSEntity> NMSEntityType<T>.registerEntityType(key: String): NMSEntityType<T> =
    NMSRegistry.a(NMSRegsitryWrapper.ENTITY_TYPE, key, this)

/**
 * Injects an entity into the server
 *
 * Originally from [paper forums](https://papermc.io/forums/t/register-and-spawn-a-custom-entity-on-1-13-x/293)
 */
fun NMSEntityTypeBuilder.injectType(
    key: String,
    extendFrom: String
): NMSEntityType<NMSEntity> { //from https://papermc.io/forums/t/register-and-spawn-a-custom-entity-on-1-13-x/293
    @Suppress("UNCHECKED_CAST") val dataTypes = NMSDataConverterRegistry.getDataFixer()
        .getSchema(DataFixUtils.makeKey(SharedConstants.getGameVersion().worldVersion))
        .findChoiceType(NMSDataConverterTypesWrapper.ENTITY).types() as MutableMap<String, Type<*>>
    if (dataTypes.containsKey("minecraft:$key")) logWarn("ALREADY CONTAINS KEY: $key")
    dataTypes["minecraft:$key"] = dataTypes["minecraft:$extendFrom"]!!

    return build(key).registerEntityType(key)
}

object NMSDataConverterTypesWrapper {
    val ENTITY = DataConverterTypes.q
}
