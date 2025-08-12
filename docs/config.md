# Config

Idofront-config lets you easily define a config
using [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization), supporting yaml, json, and other formats
automatically.

## Define a serializable config class

```kotlin
@Serializable
class ExampleConfig(
    val cooldown: Int,
    val locations: List<String>,
)
```

> :info-circle: Remember you can nest serializable classes to organize your config.

## Define a config

Use the config builder to define a config during program startup. This will automatically build and read the config for
you.

```kotlin
config<ExampleConfig>(fileName = "config") {
    // Extra config options go into the config block here
}
```

> Notice, we did not specify a file type in our file name. Idofront will look for all supported file types until it
> finds a valid extension.

## Define input source

Within our config block, we must specify one source for the file, here is a list of supported options:

- `fromPath(path: Path)` Loads the config from a path *without extension*.
- `fromPluginPath(relativePath: Path, loadDefault: Boolean)` Loads a config relative to the current plugin's config
  folder, if loadDefault is true, will also load from the jar's resources if no config file exists.
- `fromInputStream(getInputStream: (ext: String) -> InputStream?)` Given an extension, loads arbitrary input.

## Serialization options

### Shared serializers module

Specifies a serializers module, shared across different config types.

```kotlin
serializersModule {
    // Start defining options like polymorphic serialization here
}
```

### Formats

You may add or override config types by extension using the `formats` block. You should use the passed module if you
want your config to use the shared module.

```kotlin
formats { module ->
    mapOf("yml" to Yaml(serializersModule = module, ...))
}
```

[//]: # (### Default configs)

[//]: # ()

[//]: # (- `#!kotlin default&#40;ext: String, config: YourConfig /*optional*/&#41;&#41;` If the config file does not exist, will save a config using the serializer for `ext`. If `config` isn't specified, will try to use the class' default constructor.)

## Example

```kotlin
config<ExampleConfig>(fileName = "config") {
    fromPluginPath(loadDefault = true)
    
    serializersModule {
        polymorphic(SomeClass::class) {
            subclass(...)
        }
        include(someOtherModule)
    }
    mergeOnFail()
}
```

This will load a file from the plugin config called `config.<ext>`. We can add a file: `resources/config.yml` in our
project so that the loadDefault option will load it when a config file doesn't already exist.
