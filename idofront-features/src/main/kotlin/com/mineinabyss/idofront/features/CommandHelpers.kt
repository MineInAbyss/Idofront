package com.mineinabyss.idofront.features

import org.bukkit.command.CommandSender

class TabCompletion(
    val sender: CommandSender,
    val command: org.bukkit.command.Command,
    val alias: String,
    val args: Array<String>
)
