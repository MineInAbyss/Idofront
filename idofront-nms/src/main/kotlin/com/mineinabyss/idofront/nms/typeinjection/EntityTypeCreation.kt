package com.mineinabyss.idofront.nms.typeinjection

import com.mineinabyss.idofront.nms.aliases.NMSEntity
import com.mineinabyss.idofront.nms.aliases.NMSEntityType
import com.mojang.datafixers.DataFixer
import net.minecraft.util.datafix.DataConverterRegistry
import net.minecraft.world.entity.EntityTypes
import net.minecraft.world.entity.EnumCreatureType

fun <T : NMSEntity> EntityTypes.Builder<T>.build(name: String): NMSEntityType<T> = a(name)

typealias NMSEntityTypeFactory<T> = EntityTypes.b<T>

typealias NMSEntityTypeBuilder = EntityTypes.Builder<NMSEntity>

fun NMSEntityTypeBuilder.withSize(width: Float, height: Float): NMSEntityTypeBuilder = this.a(width, height)

fun NMSEntityTypeBuilder.withoutSave(): NMSEntityTypeBuilder = this.b()

fun NMSEntityTypeBuilder.withFireImmunity(): NMSEntityTypeBuilder = this.c()

fun NMSEntityTypeFactory<NMSEntity>.builderForCreatureType(creatureType: EnumCreatureType): EntityTypes.Builder<NMSEntity> =
    EntityTypes.Builder.a(this, creatureType)

object NMSDataConverterRegistry {
    fun getDataFixer(): DataFixer = DataConverterRegistry.a()
}
