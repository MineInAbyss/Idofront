package com.mineinabyss.idofront.commands

interface Permissionable {
    val permissionChain: String
    val permissions: List<String>
}