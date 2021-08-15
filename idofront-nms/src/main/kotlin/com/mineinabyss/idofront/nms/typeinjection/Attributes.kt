package com.mineinabyss.idofront.nms.typeinjection

import com.mineinabyss.idofront.nms.aliases.NMSEntityInsentient
import com.mineinabyss.idofront.nms.aliases.NMSEntityLiving
import net.minecraft.world.entity.ai.attributes.AttributeBase
import net.minecraft.world.entity.ai.attributes.AttributeProvider
import net.minecraft.world.entity.ai.attributes.GenericAttributes

typealias NMSAttributeProvider = AttributeProvider
typealias NMSAttributeBuilder = AttributeProvider.Builder
typealias NMSGenericAttributes = GenericAttributes

object NMSAttributes {
    fun emptyBuilder(): NMSAttributeBuilder = NMSAttributeProvider.a()
    fun forEntityLiving(): NMSAttributeBuilder = NMSEntityLiving.dq()
    fun forEntityInsentient(): NMSAttributeBuilder = NMSEntityInsentient.w()
}

fun NMSAttributeBuilder.set(attribute: AttributeBase, value: Double? = null): NMSAttributeBuilder {
    if (value != null) a(attribute, value)
    return this
}// else a(attribute)

fun NMSAttributeBuilder.build(): NMSAttributeProvider = a()
