package com.mineinabyss.idofront.commands.arguments

import com.mineinabyss.idofront.commands.Command


open class OptionListArg(
        name: String,
        final override val options: List<String>,
        init: CmdInit<OptionListArg>? = null
) : StringArg(name, init.cast()), OptionArg {

    init {
        parseErrorMessage = { "$name was $it, needs to be one of: $options" }
    }

    val runtimeOptions: OptionListArg.() -> List<String> = { options }

    override fun verify(execution: Command.Execution) =
            when {
                options.contains(execution.arg) -> true
                else -> {
                    execution.sendParseError()
                    false
                }
            }
}