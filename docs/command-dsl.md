# Commands

[//]: # ([:simple-gradle: ![]&#40;https://img.shields.io/maven-metadata/v?label=idofront-commands&metadataUrl=https://repo.mineinabyss.com/releases/com/mineinabyss/idofront-commands/maven-metadata.xml&#41;{ style="vertical-align:middle" }]&#40;https://repo.mineinabyss.com/#/releases/com/mineinabyss/idofront-commands&#41;)

Idofront provides a clean way of creating custom command structures through a DSL. This feature is currently very
experimental.

## Register your command executor

Implement `IdofrontCommandExecutor` It is recommended to create a singleton object for managing your commands.

```kotlin
object MyCommandExecutor: IdofrontCommandExecutor() {
    override val commands: CommandHolder = commands(MyPlugin) { //MyPlugin is a reference to your main plugin instance
        //Command DSL ishere
    }
}

...

override fun onEnable(){
    MyCommandExecutor //instantiate the singleton
}
```

That's it! The command registration is done for you. You do however currently need to add your commands into
`plugin.yml` like you normally would, but hopefully this won't be necessary in the future.

## Creating commands

Within your `commands` block, you may create new commands as follows:

```kotlin
command("basicCommand"){
}

command("anotherCommand", "alias", "anotherAlias", desc= "Short description") {
}

//or alternatively, which will be the preferred format in this guide

"basicCommand" {
}

("anotherCommand" / "alias" / "anotherAlias")(desc= "Short description") {
}
""
```

This will allow players to use `/basicCommand`, and `/anotherCommand` (or `/alias`, `/anotherAlias`)
A malformated command will display relevant help information, such as subcommands or missing arguments.
`/basicCommand ?` will display the full help message with description, aliases, etc...

## Subcommands

Let's start building towards an example. We have a plugin with a main command, and multiple subcommands which do
different things. We want to be able to do:
`/myplugin version` - To get the plugin version
`/myplugin giveApple <amount (default 1)> <player (default to self)>` - To give an apple to a certain player

We can simply nest subcommands within other commands (indefinitely) to create a structure:

```kotlin
"myplugin" {
    "version" { ... }
    "giveApple" { ... }
}
```

## Actions

To actually run things with a command, we must specify actions. Actions, like the `playerAction` may have conditions
that need to be met before they are run. If any condition ever fails, the command will stop right there, and an error
will be sent to the sender. Multiple actions may exist in a command, but upon failure, no other actions will be
executed.

If an action succeeds, it will execute the code inside, plus give some extra contextual information, such as the sender.

Let's use the default `action` for our version, which gives us access to a sender, and `playerAction` which ensures the
sender is a player, and gives us a player to work with:

```kotlin
"myplugin" {
    "version" {
        action {
            sender.info("Plugin version: ${MyPlugin.version}")
        }
    }
    "giveApple" {
        playerAction {
            player.inventory.addItem(ItemStack(Material.APPLE, 1))
        }
    }
}
```

## Arguments

Now we need to let players pass an amount for the item.

Arguments are done through delegates, there are some methods for primitives, but you may also create custom arguments:

```kotlin
"giveApple" {
    val amount by intArg { default = 1 }
    playerAction {
        player.inventory.addItem(ItemStack(Material.APPLE, amount))
    }
}
```

And that's it! If you have multiple arguments, they will be required in that order. You may also customize some other
things, like the error message within `intArg { }`. You may even use mutable properties.

Default just means the argument may be omitted. It doesn't do much if a default argument is followed by a mandated one.

## Limiting argument visibility

The general rule is, if you have access to an argument, it will be required in that command. This means, while you may
share the same arguments between several subcommands, any commands below will also require that argument to be passed (
the only limitation is that there cannot be any root level arguments yet).

To fix this, a `commandGroup` block exists, which does nothing but limit argument visibility:

```kotlin
commandGroup {
    val amount by intArg()
    //both commands below will require the "amount" to be passed
    "one" { ... }
    "two" { ... }
}
//no arguments will be required here, because we can't access any in our code!
"noArgs" { ... }
```

## Conclusion

There are some more features sprinkled into the plugin, but many might disappear. I'm more confident the DSL will remain
more or less similar going into the future, but be warned that some refactors may happen, and some things may change.

## Future plans

The current system also doesn't build any structure at startup, so it's unaware of things like arguments of subcommands,
or subcommands of subcommands. Everything is evaluated on-the-go, except root level commands, since those need to be
registered upon startup (this is why we can't currently have root level arguments). The biggest thing stopping a
structure from being generated upon startup is arguments. While it is possible to get a reference to the object the
delegate is being called from (and thus have separate instances of each argument in a map), this does not work for
lambdas, which return null as the reference. Perhaps it is possible to sidestep this with `inline` functions, thought I
have not tried it yet.
