package com.mineinabyss.idofront.nms.typeinjection

import com.mineinabyss.idofront.nms.aliases.NMSEntityInsentient
import com.mineinabyss.idofront.nms.aliases.NMSEntityLiving
import net.minecraft.server.v1_16_R2.AttributeBase
import net.minecraft.server.v1_16_R2.AttributeProvider

typealias NMSAttributeProvider = AttributeProvider
typealias NMSAttributeBuilder = AttributeProvider.Builder

object NMSAttributes {
    fun emptyBuilder(): NMSAttributeBuilder = NMSAttributeProvider.a()
    fun forEntityLiving(): NMSAttributeBuilder = NMSEntityLiving.cK()
    fun forEntityInsentient(): NMSAttributeBuilder = NMSEntityInsentient.p()
}

fun NMSAttributeBuilder.set(attribute: AttributeBase, value: Double? = null): NMSAttributeBuilder {
    if (value != null) a(attribute, value)
    return this
}// else a(attribute)

fun NMSAttributeBuilder.build(): NMSAttributeProvider = a()
