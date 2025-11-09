package com.mineinabyss.idofront.commands.brigadier

import com.mineinabyss.idofront.commands.brigadier.context.IdoCommandContext
import com.mojang.brigadier.arguments.*
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import org.bukkit.Bukkit

object Args {
    fun word() = StringArgumentType.word()
    fun string() = StringArgumentType.string()
    fun greedyString() = StringArgumentType.greedyString()

    fun bool() = BoolArgumentType.bool()

    fun double(min: Double = Double.MIN_VALUE, max: Double = Double.MAX_VALUE) =
        DoubleArgumentType.doubleArg(min, max)

    fun float(min: Float = Float.MIN_VALUE, max: Float = Float.MAX_VALUE) =
        FloatArgumentType.floatArg(min, max)

    fun integer(min: Int = Int.MIN_VALUE, max: Int = Int.MAX_VALUE) =
        IntegerArgumentType.integer(min, max)


    fun long(min: Long = Long.MIN_VALUE, max: Long = Long.MAX_VALUE) =
        LongArgumentType.longArg(min, max)

    fun offlinePlayer() = word()
        .suggests { suggestFiltering(Bukkit.getOnlinePlayers().map { it.name }) }
        .map { Bukkit.getOfflinePlayer(it) }

    fun options(list: List<String>) = string()
        .suggests { suggestFiltering(list) }
        .map {
            if (it !in list) fail("Option must be one of ${list.joinToString()}")
            it
        }
    fun options(getList: IdoCommandContext.() -> List<String>) = string()
        .suggests { suggestFiltering(getList()) }
        .map {
            val list = getList()
            if (it !in list) fail("Option must be one of ${list.joinToString()}")
            it
        }
}

typealias ArgsMinecraft = ArgumentTypes
