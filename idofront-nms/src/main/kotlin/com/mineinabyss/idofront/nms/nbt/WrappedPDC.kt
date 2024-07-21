package com.mineinabyss.idofront.nms.nbt

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtIo
import net.minecraft.nbt.Tag
import org.bukkit.NamespacedKey
import org.bukkit.craftbukkit.persistence.CraftPersistentDataAdapterContext
import org.bukkit.craftbukkit.persistence.CraftPersistentDataTypeRegistry
import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream

/**
 * A [PersistentDataContainer] that takes an NMS NBT tag. Useful for avoiding bukkit's
 * constant copying (ex on ItemStacks.)
 */
class WrappedPDC(
    val compoundTag: CompoundTag
) : PersistentDataContainer {
    private val adapterContext = CraftPersistentDataAdapterContext(DATA_TYPE_REGISTRY)

    override fun <T, Z : Any> set(key: NamespacedKey, type: PersistentDataType<T, Z>, value: Z) {
        compoundTag.put(
            key.toString(),
            DATA_TYPE_REGISTRY.wrap(type, type.toPrimitive(value, adapterContext))
        )
    }

    override fun <T, Z> has(key: NamespacedKey, type: PersistentDataType<T, Z>): Boolean =
        key.toString() in compoundTag

    override fun has(key: NamespacedKey): Boolean =
        key.toString() in compoundTag

    override fun <T : Any, Z> get(key: NamespacedKey, type: PersistentDataType<T, Z>): Z? {
        val value: Tag = compoundTag.get(key.toString()) ?: return null
        return type.fromPrimitive(DATA_TYPE_REGISTRY.extract<T, Tag>(type, value), adapterContext)
    }

    override fun <T : Any, Z : Any> getOrDefault(
        key: NamespacedKey,
        type: PersistentDataType<T, Z>,
        defaultValue: Z
    ): Z = get(key, type) ?: defaultValue

    override fun getKeys(): MutableSet<NamespacedKey> =
        compoundTag.allKeys.mapTo(mutableSetOf()) { NamespacedKey.fromString(it)!! }

    override fun remove(key: NamespacedKey) {
        compoundTag.remove(key.toString())
    }

    override fun isEmpty(): Boolean = compoundTag.isEmpty

    override fun copyTo(other: PersistentDataContainer, replace: Boolean) {
        val target = (other as? WrappedPDC)?.compoundTag ?: return
        if (replace) compoundTag.allKeys.map { it to compoundTag.get(it)!! }.forEach { target.put(it.first, it.second) }
        else target.merge(compoundTag)
    }

    override fun getAdapterContext(): PersistentDataAdapterContext = adapterContext

    override fun serializeToBytes(): ByteArray {
        val byteArrayOutput = ByteArrayOutputStream()
        DataOutputStream(byteArrayOutput).use { dataOutput ->
            NbtIo.write(compoundTag, dataOutput)
            return byteArrayOutput.toByteArray()
        }
    }

    override fun readFromBytes(bytes: ByteArray, clear: Boolean) {
        if (clear) compoundTag.allKeys.forEach(compoundTag::remove)
        DataInputStream(ByteArrayInputStream(bytes)).use { dataInput ->
            val compound = NbtIo.read(dataInput)
            compoundTag.allKeys.map { it to compoundTag.get(it)!! }.forEach { compound.put(it.first, it.second) }
        }
    }

    companion object {
        private val DATA_TYPE_REGISTRY = CraftPersistentDataTypeRegistry()
    }
}
