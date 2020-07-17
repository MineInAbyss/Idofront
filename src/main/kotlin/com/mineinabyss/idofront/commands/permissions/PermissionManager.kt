package com.mineinabyss.idofront.commands.permissions

class PermissionManager(
        override val parentPermission: String
) : Permissionable {
    override var noPermissionMessage = "You do not have permission to run this command!"
    override val permissions: MutableList<String> = mutableListOf(parentPermission)
}