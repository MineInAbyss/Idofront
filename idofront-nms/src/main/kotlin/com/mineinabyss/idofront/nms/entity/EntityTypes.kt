package com.mineinabyss.idofront.nms.entity

import com.mineinabyss.idofront.nms.aliases.NMSCreatureType
import com.mineinabyss.idofront.nms.aliases.NMSEntityType
import com.mineinabyss.idofront.nms.aliases.toNMS
import org.bukkit.NamespacedKey
import org.bukkit.entity.Entity

/** The name of the mob type as registered in Minecraft, ex. `entity.minecraft.zombie`. */
val NMSEntityType<*>.keyName: String get() = this.g()

/** The type's [keyName] without the `entity.<namespace>.` prefix */
val NMSEntityType<*>.typeName: String get() = this.keyName.removePrefix("entity.").substringBefore(".")

/** The [typeName] of this creature's [NMSEntityType]. */
val Entity.typeName get() = toNMS().entityType.typeName

/** Gets a namespaced key via the NMS entity type's id. */
val Entity.typeNamespacedKey: NamespacedKey get() {
    val typeId = toNMS().entityType.id
    val (namespace, key) = runCatching { typeId.split(":") }.getOrElse {
        //TODO I'm unsure if this is a feature of mc that all minecraft ids are not namespaced or if we are
        // registering wrong here.
        listOf("minecraft", typeId)
    }
    @Suppress("DEPRECATION")
    return NamespacedKey(namespace, key)
}

/** The entity type's [NMSCreatureType]. */
val NMSEntityType<*>.creatureType: NMSCreatureType get() = this.f()

/** The name of the [NMSCreatureType] of this entity. */
val Entity.creatureType get() = toNMS().entityType.creatureType.name

/** Whether this mob's creature type (i.e. monster, creature, water_creature, ambient, misc) is [creatureType] */
fun Entity.isOfCreatureType(creatureType: NMSCreatureType) = toNMS().entityType.creatureType.name == creatureType.name
