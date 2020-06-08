package com.mineinabyss.idofront.commands

interface Permissionable {
    val parentPermission: String
    val permissions: MutableList<String>
}