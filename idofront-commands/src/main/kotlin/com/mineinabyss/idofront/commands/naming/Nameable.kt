package com.mineinabyss.idofront.commands.naming

interface Nameable {
    val nameChain: List<String>
    val names: List<String>
    val description: String
}