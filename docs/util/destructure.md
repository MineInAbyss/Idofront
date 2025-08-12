# Destructure

> Read about this Kotlin feature
> in [Kotlin Docs - Destructuring Declarations](https://kotlinlang.org/docs/destructuring-declarations.html)

Sometimes it is convenient to destructure an object into a number of variables, for example:

```kotlin
val (name, age) = person
```

In spigot, this pattern is very convenient for events, locations, and more. Idofront provides destructors for many
direct subclasses of the `Event` class, allowing for the following:

```kotlin
fun onLeash(event: PlayerLeashEntityEvent) {
    val (player, entity, leashHolder) = event
}
```

And for locations or velocity:

```kotlin
val (x, y, z, world) = player.location
val (vx, vy, vz) = player.velocity
```

Note: You do not need to specify all the components:

```kotlin
val (x) = player.velocity
```

Also of note, you need to import these extension functions for this to work, otherwise your IDE will show an error!
