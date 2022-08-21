package com.mineinabyss.idofront.nms.nbt

import com.mineinabyss.idofront.nms.aliases.toNMS
import net.minecraft.nbt.CompoundTag
import org.bukkit.inventory.ItemStack

val net.minecraft.world.item.ItemStack.hasPDC: Boolean get() = tag?.contains("PublicBukkitValues") == true
val net.minecraft.world.item.ItemStack.fastPDC: WrappedPDC?
    get() {
        return WrappedPDC((tag?.get("PublicBukkitValues") ?: return null) as CompoundTag)
    }

val ItemStack.fastPDC get() = toNMS()?.fastPDC
