package com.mineinabyss.idofront.di

import kotlin.reflect.KClass

class ScopedDIContext(
    val simpleName: String,
    val byClass: KClass<*>? = null,
) : DIContext()
