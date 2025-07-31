package com.mineinabyss.idofront.nms.entities

import io.papermc.paper.adventure.PaperAdventure
import net.kyori.adventure.text.Component
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket
import net.minecraft.world.inventory.MerchantMenu
import net.minecraft.world.item.trading.Merchant
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.craftbukkit.inventory.CraftContainer
import org.bukkit.craftbukkit.inventory.view.CraftMerchantView
import org.bukkit.entity.Player
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.view.MerchantView

private val MerchantMenuTrader = MerchantMenu::class.java.getDeclaredField("trader").apply { isAccessible = true }

fun InventoryView.title(title: Component) {
    val serverPlayer = (player as CraftPlayer).handle
    val containerId = serverPlayer.containerMenu.containerId

    when {
        this is MerchantView -> {
            val clientMerchant = MerchantMenuTrader.get((this as CraftMerchantView).handle) as Merchant
            clientMerchant.openTradingScreen(serverPlayer, PaperAdventure.asVanilla(title), 0)
        }

        else -> {
            val windowType = CraftContainer.getNotchInventoryType(topInventory)
            serverPlayer.connection.send(
                ClientboundOpenScreenPacket(
                    containerId,
                    windowType,
                    PaperAdventure.asVanilla(title)
                )
            )
            (player as Player).updateInventory()
        }
    }
}
