package com.mineinabyss.idofront.config

import com.mineinabyss.idofront.messaging.error
import com.mineinabyss.idofront.messaging.success
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

@Suppress("MemberVisibilityCanBePrivate")
data class ReloadScope(
        val sender: CommandSender
) {
    val consoleSender = Bukkit.getConsoleSender()

    fun attempt(task: String, block: () -> Unit) {
        attempt(task, task, block)
    }

    fun attempt(success: String, fail: String, block: () -> Unit) {
        try {
            block()
            sender.success(success)
            if (sender != consoleSender) consoleSender.success(success)
        } catch (e: Exception) {
            sender.error(fail)
            if (sender != consoleSender) consoleSender.error(fail)
            e.printStackTrace()
            return
        }
    }

}
