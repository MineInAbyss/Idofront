package com.mineinabyss.idofront.time

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

val Int.ticks: Duration get() = (this * 50).milliseconds
val Long.ticks: Duration get() = (this * 50).milliseconds

val Duration.inWholeTicks: Long get() = this.inWholeMilliseconds / 50
