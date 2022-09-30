package com.mineinabyss.idofront.textcomponents

import net.kyori.adventure.text.Component

operator fun Component.plus(other: Component): Component = append(other)
