package com.mineinabyss.idofront.commands.arguments

import com.mineinabyss.idofront.commands.GenericCommand


open class StringListArg(
        name: String,
        final override val options: List<String>,
        init: CmdInit<StringListArg>? = null
) : StringArg(name, init.cast()), OptionArg {

    init {
        parseErrorMessage = { "$name was $it, needs to be one of: $options" }
    }

    val runtimeOptions: StringListArg.() -> List<String> = { options }

    override fun verify(command: GenericCommand) =
            when {
                options.contains(command.arg) -> true
                else -> {
                    command.sendParseError()
                    false
                }
            }
}