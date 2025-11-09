package com.mineinabyss.idofront.examples.commands

import com.mineinabyss.idofront.commands.brigadier.*
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

                // Only one execution block will run
                executes {
                    sender.sendMessage("You shouldn't see this message!")
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
        // This will also add an argument to the end of the command for executing it on other players
        // ex. /example tpUp SomePlayer
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
                // Some may require you to call .resolve due to brigader internals.
                ArgsMinecraft.player().resolve(),
                // Args are generic argument helpers provided by idofront
                Args.greedyString()
            ) { sendTo, message ->
                // Some built-in arguments like players return a list when resolved
                sendTo.first().sendMessage(message)
            }
        }

        "msgFancy" {
            executes.args(
                // Arguments can be given names, default values, and tab completion suggestiosn
                ArgsMinecraft.player().resolve()
                    .named("player"),
                Args.greedyString()
                    .named("message")
                    // Note that default arguments can't have arguments without defaults after them.
                    .default { "Hello from ${sender.name}" }
                    .suggests {
                        suggest("Hello")
                        if (input.startsWith("Good")) suggest("Goodbye")
                    },
            ) { sendTo, message ->
                sendTo.first().sendMessage(message)
            }
        }

        val options = listOf("A", "B", "C")

        // Arguments can be parsed using `map`, which can also prevent the command from running when they are invalid.
        "parsing" {
            executes.args(
                Args.word().named("option")
                    // We can suggest options from a list, automatically filtering by user input
                    .suggests { suggestFiltering(options) }
                    .map { if (it !in options) fail("Option must be one of $options") else it }
            ) { option ->
                sender.sendMessage("You chose $option")
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
