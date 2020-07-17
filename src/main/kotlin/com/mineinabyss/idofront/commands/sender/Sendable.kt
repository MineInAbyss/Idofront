package com.mineinabyss.idofront.commands.sender

import org.bukkit.command.CommandSender

interface Sendable {
    val sender: CommandSender
}