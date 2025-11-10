package com.mineinabyss.idofront.examples.commands

import com.mineinabyss.idofront.commands.brigadier.*
import io.papermc.paper.math.BlockPosition
import org.bukkit.plugin.java.JavaPlugin

class ExampleCommands {
    fun registerCommands(plugin: JavaPlugin) {
        // Commands are registered by calling plugin.commands in the plugin's
        // onEnable or onLoad block.
        plugin.commands {
            // Register a top-level `example` command
            "example" {
                // Specify what happens when running this command
                executes {
                    sender.sendMessage("Ran /example!")
                }

                // If multiple executes blocks are defiend at the same path, only the last will run
                executes {
                    sender.sendMessage("You won't see the previous message!")
                }

                // Subcommands are defined exactly like top-level commands
                "nested" {
                    executes {
                        sender.sendMessage("Ran /example nested!")
                    }
                }

                // It's often helpful to define commands in separate functions to split up code
                // Let's continue the examples there!
                exampleSubcommands()
                arguments()
                permissions()
                test()
            }
        }
    }

    fun IdoCommand.exampleSubcommands() {
        // Registers a subcommand called nested2. In theory this doesn't care about
        // the parent command. We can even use it to nest subcommands in subcommands.
        "nested2" {
            executes {
                sender.sendMessage("Ran /example nested2!")
            }
        }

        // Commands can be restricted to only execute on players.
        "tpUp" {
            executes.asPlayer {
                player.teleport(player.location.add(0.0, 1.0, 0.0))
            }
        }
    }

    fun IdoCommand.arguments() {
        "msg" {
            // Commands register arguments in the executes block
            executes.args(
                // ArgsMinecraft lets you access Minecraft-specific command types
                // Some may require you to call .resolve due to brigadier internals.
                "player" to ArgsMinecraft.player().resolve(),
                // Args are generic argument helpers provided by idofront
                "message" to Args.greedyString()
            ) { sendTo, message ->
                // Some built-in arguments like players return a list when resolved
                sendTo.first().sendMessage(message)
            }
        }

        "msgFancy" {
            executes.args(
                // Arguments can be given default values, and tab completion suggestiosn
                "player" to ArgsMinecraft.player().resolve(),
                "message" to Args.greedyString()
                    // Note that default arguments can't have arguments without defaults after them.
                    .default { "Hello from ${sender.name}" }
                    .suggests {
                        suggest("Hello")
                        if (argument.startsWith("Good")) suggest("Goodbye")
                    },
            ) { sendTo, message ->
                sendTo.first().sendMessage(message)
            }
        }

        val options = listOf("A", "B", "C")

        // Arguments can be parsed using `map`, which can also prevent the command from running when they are invalid.
        "parsing" {
            executes.args(
                "option" to Args.word()
                    // We can suggest options from a list, automatically filtering by user input
                    .map { if (it !in options) fail("Option must be one of $options") else it }
                    .suggests { suggestFiltering(options) }
            ) { option ->
                sender.sendMessage("You chose $option")
            }
        }
    }

    fun IdoCommand.test() {
        "test" {
            // Any number of arguments can be passed in this example since all have a default value
            "optionals" {
                executes.asPlayer().args(
                    "block" to ArgsMinecraft.blockPosition().resolve().default { BlockPosition.BLOCK_ZERO },
                    "message" to Args.string().default { "hello world" }
                ) { block, message ->
                    println("Hello $message from $block")
                }
            }
            // If two branches have an argument of different names, brigadier can sometimes resolve either, ex. player OR location
            "tp" {
                executes.asPlayer().args(
                    "player" to ArgsMinecraft.players().resolve()
                ) {
                    player.teleport(it.first().location)
                }

                executes.asPlayer().args(
                    "block" to ArgsMinecraft.blockPosition().resolve()
                ) {
                    player.teleport(it.toLocation(player.world))
                }

            }
        }
    }

    /**
     * Idofront commands automatically register permissions based on their parent command names,
     * ex. the nested command can use `example.nested` or `example.*`
     */
    fun IdoCommand.permissions() {
        //TODO examples with permissions
    }
}
