package com.mineinabyss.idofront.commands.arguments

import com.mineinabyss.idofront.commands.BaseCommand


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
            parseErrorMessage = { "$name can only be true or false, not $passed" }
            missingMessage = { "Please input whether $name is true or false" }
            parseBy { passed.toBoolean() }
            initWith(init)
        }

/** An argument which can be any of a specific set of [options] */
fun BaseCommand.optionArg(options: List<String>, init: (CommandArgument<String>.() -> Unit)? = null) =
        stringArg {
            parseErrorMessage = { "$name needs to be one of $options" }
            verify { options.contains(passed) }
            initWith(init)
        }