package com.mineinabyss.idofront.nms.entity

import com.mineinabyss.idofront.nms.aliases.NMSCreatureType
import com.mineinabyss.idofront.nms.aliases.NMSEntityType
import com.mineinabyss.idofront.nms.aliases.toNMS
import org.bukkit.entity.Entity

/** The name of the mob type as registered in Minecraft, ex. `entity.minecraft.zombie`. */
val NMSEntityType<*>.keyName: String get() = this.g()

/** The type's [keyName] without the `entity.minecraft.` prefix */
val NMSEntityType<*>.typeName: String get() = this.keyName.removePrefix("entity.minecraft.")

/** The [typeName] of this creature's [NMSEntityType]. */
val Entity.typeName get() = toNMS().entityType.typeName

/** The entity type's [NMSCreatureType]. */
val NMSEntityType<*>.creatureType: NMSCreatureType get() = this.f()

/** The name of the [NMSCreatureType] of this entity. */
val Entity.creatureType get() = toNMS().entityType.creatureType.name

/** Whether this mob's creature type (i.e. monster, creature, water_creature, ambient, misc) is [creatureType] */
fun Entity.isOfCreatureType(creatureType: NMSCreatureType) = toNMS().entityType.creatureType.name == creatureType.name
