# NMS

[//]: # ([:simple-gradle: ![]&#40;https://img.shields.io/maven-metadata/v?label=idofront-nms&metadataUrl=https://repo.mineinabyss.com/releases/com/mineinabyss/idofront-nms/maven-metadata.xml&#41;{ style="vertical-align:middle" }]&#40;https://repo.mineinabyss.com/#/releases/com/mineinabyss/idofront-nms&#41;)

```kotlin
implementation("com.mineinabyss:idofront-nms:<version>")
```

## Conversions

We provide `toBukkit` and `toNMS` extension functions for some common NMS types, as well as typealiases starting with
NMS (ex `NMSItemStack`.)

Classes include: Entity, Player, World, Inventory, ItemStack

Example:

```kotlin
bukkitEntity.toNMS() // returns NMSEntity
nmsEntity.toBukkit() // returns BukkitEntity
```

## WrappedPDC

A persistent data container (PDC) implementation that wraps a `CompoundTag`.

### Fast Item PDC

We include an extension `ItemStack.fastPDC` which will bypass bukkit's meta cloning that adds a lot of overhead when
dealing with big PDCs and takes the nbt directly from NMS.
