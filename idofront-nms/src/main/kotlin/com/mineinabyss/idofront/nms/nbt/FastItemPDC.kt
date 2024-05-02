package com.mineinabyss.idofront.nms.nbt

import com.mineinabyss.idofront.nms.aliases.NMSItemStack
import com.mineinabyss.idofront.nms.aliases.toNMS
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import org.bukkit.inventory.ItemStack

val NMSItemStack.hasPDC: Boolean get() = components.get(DataComponents.CUSTOM_DATA)?.unsafe?.contains("PublicBukkitValues") == true
val NMSItemStack.fastPDC: WrappedPDC?
    get() {
        return WrappedPDC(components.get(DataComponents.CUSTOM_DATA)?.unsafe?.getCompound("PublicBukkitValues") ?: return null)
    }

val ItemStack.fastPDC get() = toNMS()?.fastPDC
