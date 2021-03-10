package com.mineinabyss.idofront.nms.typeinjection

import com.mineinabyss.idofront.nms.aliases.NMSEntity
import com.mineinabyss.idofront.nms.aliases.NMSEntityType
import com.mojang.datafixers.DataFixer
import net.minecraft.server.v1_16_R2.DataConverterRegistry
import net.minecraft.server.v1_16_R2.Entity
import net.minecraft.server.v1_16_R2.EntityTypes
import net.minecraft.server.v1_16_R2.EnumCreatureType

fun <T : NMSEntity> EntityTypes.Builder<T>.build(name: String): NMSEntityType<T> = a(name)

typealias NMSEntityTypeFactory<T> = EntityTypes.b<T>

typealias NMSEntityTypeBuilder = EntityTypes.Builder<Entity>

fun NMSEntityTypeBuilder.withSize(width: Float, height: Float): NMSEntityTypeBuilder = this.a(width, height)

fun NMSEntityTypeBuilder.withoutSave(): NMSEntityTypeBuilder = this.b()

fun NMSEntityTypeBuilder.withFireImmunity(): NMSEntityTypeBuilder = this.c()

fun NMSEntityTypeFactory<Entity>.builderForCreatureType(creatureType: EnumCreatureType): EntityTypes.Builder<Entity> =
    EntityTypes.Builder.a(this, creatureType)

object NMSDataConverterRegistry {
    fun getDataFixer(): DataFixer = DataConverterRegistry.a()
}
