package com.mineinabyss.idofront

import com.mineinabyss.idofront.commands.brigadier.Args
import com.mineinabyss.idofront.commands.brigadier.ArgsMinecraft
import com.mineinabyss.idofront.commands.brigadier.commands
import com.mineinabyss.idofront.commands.brigadier.executes
import com.mineinabyss.idofront.di.DI
import com.mineinabyss.idofront.plugin.listeners
import com.mineinabyss.idofront.serialization.recipes.options.IngredientOptionsListener
import kotlinx.coroutines.flow.merge
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class IdofrontPlugin : JavaPlugin() {
    override fun onEnable() {
        val recipeOptionsListener = IngredientOptionsListener(this)
        DI.add(recipeOptionsListener)
        listeners(recipeOptionsListener)

        commands {
            "idofront" {
                "msg" {
                    executes(
                        Args.string(),
                        ArgsMinecraft.player().resolve()
                            .map { it.single() }
                            .default { sender as? Player ?: fail("Receiver should be player") },
                        Args.integer(min = 0).default { 1 }
                    ) { msg, player, times ->
                        repeat(times) {
                            player.sendMessage(msg)
                        }
                    }
                }
            }
        }
    }
}
