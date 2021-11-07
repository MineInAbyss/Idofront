package com.mineinabyss.idofront.commands.permissions

import com.mineinabyss.idofront.commands.BaseCommand
import com.mineinabyss.idofront.messaging.error

interface Permissionable {
    val parentPermission: String
    var permissions: MutableList<String>

    fun permissionsMetFor(command: BaseCommand): Boolean {
        if (permissions.none { command.sender.hasPermission(it) || command.sender.hasPermission("$it.*") }) {
            command.sender.error(noPermissionMessage)
            return false
        }
        return true
    }

    //MUTABLE STUFF FOR DSL
    var permission
        get() = permissions[0]
        set(perm) = permissions.run { clear(); add(perm) }
    var noPermissionMessage: String
}
