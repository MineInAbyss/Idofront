<div align="center">
  
# idofront-platform-loader

[![Maven](https://badgen.net/maven/v/metadata-url/repo.mineinabyss.com/releases/com/mineinabyss/idofront-platform-loader/maven-metadata.xml)](https://repo.mineinabyss.com/releases/com/mineinabyss/idofront-platform-loader)
[![Discord](https://badgen.net/discord/members/QXPCk2y)](https://discord.gg/QXPCk2y)
</div>

A tiny Java project that lets several plugins load and share jar files in an isolated way.

Under the hood, we inject a classloader into Spigot's library loader system.

# Usage

#### build.gradle.kts
```kotlin
repositories {
    maven("https://repo.mineinabyss.com/releases")}
}

dependencies {
    // Included in idofront, or add a specific dependency:
    implementation("com.mineinabyss:idofront-platform-loader:x.y.z")
}
```
Make sure to use the shadowJar plugin to shade this dependency.

#### Plugin
```kotlin
override fun onLoad() {
    // Load a .platform file in the plugin folder that starts with "mineinabyss"
    IdofrontPlatforms.load(this, "mineinabyss")
    
    // Alternatively, write your own predicate to check which file to load (remember you can't use Kotlin stdlib until after this line)
    IdofrontPlatforms.load(this) { file -> Boolean }
}
```
