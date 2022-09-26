package com.mineinabyss.idofront.commands.extensions.actions

import com.mineinabyss.idofront.commands.Command
import com.mineinabyss.idofront.commands.execution.Action

fun <A : Action> Command.customAction(run: A.() -> Unit, create: Command.() -> A) {
    if (canExecute()) create(this).execute(run)
}