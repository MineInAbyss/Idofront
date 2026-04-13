# Commands

[//]: # ([:simple-gradle: ![]&#40;https://img.shields.io/maven-metadata/v?label=idofront-commands&metadataUrl=https://repo.mineinabyss.com/releases/com/mineinabyss/idofront-commands/maven-metadata.xml&#41;{ style="vertical-align:middle" }]&#40;https://repo.mineinabyss.com/#/releases/com/mineinabyss/idofront-commands&#41;)

Idofront provides a commands DSL that wraps Minecraft's Brigadier system.
We show an example implementation in the `examples` module on GitHub.

## Register your command executor

In your plugin's `onEnable`, call the `commands` block.
Brigadier only lets you register commands on server startup, so your structure can't be changed after this:

```kotlin
plugin.commands {
    // Registers a top level /example command
    "example" {
        ...
    }
}
```

## Creating commands

Within your `commands` block, you may create new commands as follows:

```kotlin
// Command with description and permission set
"basicCommand" {
    description = "Short description"
    permission = "my.plugin.perm"
}

// Command with aliases
("anotherCommand" / "alias" / "anotherAlias") {
    ...
}
""
```

This will allow players to use `/basicCommand`, and `/anotherCommand` (or `/alias`, `/anotherAlias`)
Note that subcommands default to using permission `parentpermission.<commandName>`.

## Subcommands

Let's start building towards an example. We have a plugin with a main command, and multiple subcommands which do
different things. We want to be able to do:
`/myplugin version` - To get the plugin version
`/myplugin giveApple <amount (default 1)> <player (default to self)>` - To give an apple to a certain player

We can nest subcommands within other commands (indefinitely) to create a structure.
Under the hood this chains together Brigadier's `literal`.

```kotlin
"myplugin" {
    "version" { ... }
    "giveApple" { ... }
}
```

## Executes blocks

We specify what a command does with an `executes` block. Idofront provides some helpers on this to ensure
a sender is a player and to pass command arguments.

```kotlin
"myplugin" {
    "version" {
        executes {
            sender.sendMessage("Plugin version: ${MyPlugin.version}")
        }
    }
    "giveApple" {
        executes.asPlayer {
            player.inventory.addItem(ItemStack(Material.APPLE, 1))
        }
    }
}
```

Note that we can also `fail("Message")` inside executes blocks to stop the command at any point and send the sender a message.

## Arguments

Now we need to let players pass an amount for the item.
Arguments take normal Brigadier argument types, with our own DSL around them for providing suggestions or
restricting options. We provide a singleton `Args` you can use to see a list of argument options, as well as `ArgsMinecraft`
which is an alias for Paper's custom argumetns for things like Player, Location, etc...

```kotlin
"giveApple" {
    executes.asPlayer().args(
        "amount" to Args.integer(min = 1).default { 1 },
        "player" to Args.otherPlayer() // A built-in helper in Idofront for an optional target player 
    ) { amount, other ->
        other.inventory.addItem(ItemStack(Material.APPLE, amount))
    }
}
```

Please be sure to look at the [ExampleCommands](https://github.com/MineInAbyss/Idofront/blob/master/examples/src/main/kotlin/com/mineinabyss/idofront/examples/commands/ExampleCommands.kt)
implementation for more tips around using Minecraft arguments, providing suggestions, and more!