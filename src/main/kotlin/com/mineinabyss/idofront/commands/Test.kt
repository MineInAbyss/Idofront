package com.mineinabyss.idofront.commands

import org.bukkit.plugin.java.JavaPlugin
import kotlin.reflect.KProperty

fun main() {
commands(JavaPlugin.getPlugin(/*plugin here*/)) {
    command("mobzy") {
        //the variables are stored for the commands below only
        val entityType by CommandArgument(1)
        val radius by CommandArgument(2)

        onExecute {
            radius //I can still access it here so dunno how well this will work :/
        }

        command("remove") {
            //mobzy remove radius
            onExecute {
                args //you can do stuff with these two
                sender
                radius //this will read it properly since it's delegated to CommandArgument
            }
        }
        command("info") {
            onExecute {
                radius //we can use the same argument here too!
            }

        }
    }
}
}

class CommandArgument(val num: Int) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String? {
        if(thisRef !is Command.Execution) return null
        thisRef.args
        return "$thisRef, thank you for delegating '${property.name}' to me!".also { println(it) }
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        println("$value has been assigned to '${property.name}' in $thisRef.")
    }
}