package com.mineinabyss.idofront.nms.nbt

import net.kyori.adventure.key.Key
import net.minecraft.nbt.ByteArrayTag
import net.minecraft.nbt.ByteTag
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.DoubleTag
import net.minecraft.nbt.FloatTag
import net.minecraft.nbt.IntArrayTag
import net.minecraft.nbt.IntTag
import net.minecraft.nbt.LongArrayTag
import net.minecraft.nbt.LongTag
import net.minecraft.nbt.ShortTag
import net.minecraft.nbt.StringTag
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
    val compoundTag: CompoundTag,
) : PersistentDataContainer {
    private val adapterContext = CraftPersistentDataAdapterContext(DATA_TYPE_REGISTRY)

    override fun <T, Z : Any> set(key: NamespacedKey, type: PersistentDataType<T, Z>, value: Z) {
        compoundTag.put(
            key.toString(),
            DATA_TYPE_REGISTRY.wrap(type, type.toPrimitive(value, adapterContext))
        )
    }

    override fun <T : Any, Z : Any> has(key: NamespacedKey, type: PersistentDataType<T, Z>): Boolean =
        key.toString() in compoundTag

    override fun has(key: NamespacedKey): Boolean =
        key.toString() in compoundTag

    override fun <T : Any, Z : Any> get(key: NamespacedKey, type: PersistentDataType<T, Z>): Z? {
        val value: Tag = compoundTag.get(key.toString()) ?: return null
        return type.fromPrimitive(DATA_TYPE_REGISTRY.extract<T, Tag>(type, value), adapterContext)
    }

    override fun <T : Any, Z : Any> getOrDefault(
        key: NamespacedKey,
        type: PersistentDataType<T, Z>,
        defaultValue: Z,
    ): Z = get(key, type) ?: defaultValue

    override fun getKeys(): MutableSet<NamespacedKey> =
        compoundTag.keySet().mapTo(mutableSetOf()) { NamespacedKey.fromString(it)!! }

    override fun remove(key: NamespacedKey) {
        compoundTag.remove(key.toString())
    }

    override fun isEmpty(): Boolean = compoundTag.isEmpty

    override fun copyTo(other: PersistentDataContainer, replace: Boolean) {
        val target = (other as? WrappedPDC)?.compoundTag ?: return
        if (replace) compoundTag.keySet().forEach { key ->
            target.put(key, compoundTag[key]!!)
        }
        else compoundTag.keySet().forEach { key ->
            if (target.contains(key)) return@forEach
            target.put(key, compoundTag[key]!!)
        }
    }

    override fun getPrimitiveStorageType(key: Key): PersistentDataType<*, *>? = when (compoundTag.get(key.asString())) {
        is ByteTag -> PersistentDataType.BYTE
        is ShortTag -> PersistentDataType.SHORT
        is IntTag -> PersistentDataType.INTEGER
        is LongTag -> PersistentDataType.LONG
        is FloatTag -> PersistentDataType.FLOAT
        is DoubleTag -> PersistentDataType.DOUBLE
        is StringTag -> PersistentDataType.STRING
        is ByteArrayTag -> PersistentDataType.BYTE_ARRAY
        is IntArrayTag -> PersistentDataType.INTEGER_ARRAY
        is LongArrayTag -> PersistentDataType.LONG_ARRAY
        is CompoundTag -> PersistentDataType.TAG_CONTAINER
        else -> null
    }

    override fun getAdapterContext(): PersistentDataAdapterContext = adapterContext

    override fun serializeToBytes(): ByteArray {
        val byteArrayOutput = ByteArrayOutputStream()
        DataOutputStream(byteArrayOutput).use { dataOutput ->
            NbtIo.write(compoundTag, dataOutput)
            return byteArrayOutput.toByteArray()
        }
    }

    override fun getSize(): Int {
        return compoundTag.size()
    }

    override fun readFromBytes(bytes: ByteArray, clear: Boolean) {
        if (clear) compoundTag.keySet().forEach(compoundTag::remove)
        DataInputStream(ByteArrayInputStream(bytes)).use { dataInput ->
            val compound = NbtIo.read(dataInput)
            compound.keySet().forEach { key ->
                compoundTag.put(key, compound[key]!!)
            }
        }
    }

    companion object {
        private val DATA_TYPE_REGISTRY = CraftPersistentDataTypeRegistry()
    }
}
