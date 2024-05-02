package com.mineinabyss.idofront.nms.nbt

import com.mineinabyss.idofront.nms.aliases.NMSItemStack
import com.mineinabyss.idofront.nms.aliases.toNMS
import net.minecraft.core.component.DataComponents
import org.bukkit.inventory.ItemStack

val NMSItemStack.hasPDC: Boolean get() = components.get(DataComponents.CUSTOM_DATA)
    ?.contains("PublicBukkitValues") == true

val NMSItemStack.fastPDC: WrappedPDC?
    get() {
        val customData = components.get(DataComponents.CUSTOM_DATA)
            ?.unsafe
            ?.getCompound("PublicBukkitValues")
            ?: return null
        return WrappedPDC(customData)
    }

val ItemStack.fastPDC get() = toNMS()?.fastPDC
