package com.mineinabyss.idofront.messaging

import com.mineinabyss.idofront.di.DI

val idofrontLogger by DI.scoped("Idofront").observeLogger()
