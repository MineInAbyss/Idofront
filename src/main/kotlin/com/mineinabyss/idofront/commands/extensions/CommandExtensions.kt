package com.mineinabyss.idofront.commands.extensions

import com.mineinabyss.idofront.commands.Command

fun Command.ifCanExecute(run: Command.() -> Unit) {
    if (canExecute())
        run()
}