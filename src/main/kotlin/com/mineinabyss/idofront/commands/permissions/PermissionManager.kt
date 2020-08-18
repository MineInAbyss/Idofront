package com.mineinabyss.idofront.commands.permissions

class PermissionManager(
        override val parentPermission: String,
        commandName: String
) : Permissionable {
    override var noPermissionMessage = "You do not have permission to run this command!"
    override var permissions: MutableList<String> = mutableListOf("$parentPermission.$commandName")
}