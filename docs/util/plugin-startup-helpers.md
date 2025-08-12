# Plugin startup helpers

Here's an excerpt from another plugin using some helper methods in onEnable()

```kotlin
override fun onEnable() {
    //services register one by one
    service<PlayerManager>(PlayerManagerImpl())

    //registering a list of event listeners
    listeners(
        MovementListener(),
        PlayerListener()
    )
}
```
