package com.mineinabyss.idofront.nms.nbt

import com.mineinabyss.idofront.nms.aliases.toNMS
import net.minecraft.nbt.CompoundTag
import org.bukkit.inventory.ItemStack

val net.minecraft.world.item.ItemStack.hasPDC: Boolean get() = tag?.contains("PublicBukkitValues") == true
val net.minecraft.world.item.ItemStack.fastPDC get() = if (hasPDC) WrappedPDC(tag?.get("PublicBukkitValues") as CompoundTag) else null
val ItemStack.fastPDC get() = toNMS()?.fastPDC
