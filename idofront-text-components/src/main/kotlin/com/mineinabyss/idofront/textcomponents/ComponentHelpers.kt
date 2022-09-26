package com.mineinabyss.idofront.textcomponents

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration

operator fun Component.plus(other: Component): Component =
    Component.join(JoinConfiguration.noSeparators(), this, other)
