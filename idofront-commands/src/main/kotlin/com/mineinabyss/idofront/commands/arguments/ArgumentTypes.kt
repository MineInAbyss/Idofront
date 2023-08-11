package com.mineinabyss.idofront.commands.arguments

import com.mineinabyss.idofront.commands.BaseCommand
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.Player


/** An argument parsed as an [Int] */
fun BaseCommand.intArg(init: (CommandArgument<Int>.() -> Unit)? = null) =
    arg<Int> {
        parseErrorMessage = { "$passed is not a valid integer for the $name" }
        missingMessage = { "Please input an integer for the $name" }
        parseBy { passed.toInt() }
        initWith(init)
    }

/** An argument parsed as a [String] */
fun BaseCommand.stringArg(init: (CommandArgument<String>.() -> Unit)? = null) =
    arg<String> {
        missingMessage = { "Please input the $name" }
        parseBy { passed }
        initWith(init)
    }

/** An argument parsed as a [Boolean] */
fun BaseCommand.booleanArg(init: (CommandArgument<Boolean>.() -> Unit)? = null) =
    arg<Boolean> {
        val trueOptions = listOf("true", "yes", "y", "on", "enable")
        val falseOptions = listOf("false", "no", "n", "off", "disable")
        parseErrorMessage = { "$name should be one of ${(trueOptions + falseOptions).joinToString(",")}, not $passed" }
        missingMessage = { "Please input whether $name is true or false" }
        parseBy {
            return@parseBy when (passed) {
                in trueOptions -> true
                in falseOptions -> false
                else -> error("Could not parse message")
            }
        }
        initWith(init)
    }

/** An argument which can be any of a specific set of [options] */
fun BaseCommand.optionArg(options: List<String>, init: (CommandArgument<String>.() -> Unit)? = null) =
    stringArg {
        parseErrorMessage = { "$name needs to be one of $options" }
        verify { options.contains(passed) }
        initWith(init)
    }

/** An argument which can be any player */
fun BaseCommand.playerArg(init: (CommandArgument<Player>.() -> Unit)? = null) =
    arg<Player> {
        parseErrorMessage = { "$passed is not a valid player" }
        missingMessage = { "Please input a player for the $name" }
        parseBy { Bukkit.getPlayer(passed)!! }
        initWith(init)
    }

/**
 *  An argument which can be any offline or online player
 *  This will return an object even if the player does not exist.
 *  To this method, all players will exist
 *  */
fun BaseCommand.offlinePlayerArg(init: (CommandArgument<OfflinePlayer>.() -> Unit)? = null) =
    arg<OfflinePlayer> {
        parseErrorMessage = { "$passed is not a valid player" }
        missingMessage = { "Please input a player for the $name" }
        parseBy { Bukkit.getOfflinePlayer(passed) }
        initWith(init)
    }

fun BaseCommand.entityArg(init: (CommandArgument<List<Entity>>.() -> Unit)? = null) =
    arg<List<Entity>> {
        parseErrorMessage = { "$passed is not a valid entity" }
        missingMessage = { "Please input an entity for the $name" }
        parseBy { Bukkit.selectEntities(sender, passed).toList() }
        initWith(init)
    }

fun BaseCommand.locationArg(world: World = Bukkit.getWorld("world")!!, init: (CommandArgument<Location>.() -> Unit)? = null) =
    arg<Location> {
        parseErrorMessage = { "$passed is not a valid location" }
        missingMessage = { "Please input a location for the $name" }
        parseBy {
            val (x,y,z) = passed.split(",").take(3).map { it.toDouble() }
            val worldArg = Bukkit.getWorld(passed.split(",").getOrNull(3) ?: "") ?: world
            Location(worldArg, x, y, z)
        }
        initWith(init)
    }

fun BaseCommand.worldArg(init: (CommandArgument<World>.() -> Unit)? = null) =
    arg<World> {
        parseErrorMessage = { "$passed is not a valid world" }
        missingMessage = { "Please input a world for the $name" }
        parseBy { Bukkit.getWorld(passed)!! }
        initWith(init)
    }
