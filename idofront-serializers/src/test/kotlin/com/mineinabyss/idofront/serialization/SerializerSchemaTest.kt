package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.util.DoubleRange
import com.mineinabyss.jsonschema.dsl.jsonSchema
import com.mineinabyss.jsonschema.generator.KotlinxSerializationJsonSchemaGenerator
import com.mineinabyss.jsonschema.writer.SchemaDescriptorScanner
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import net.kyori.adventure.util.TriState
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.enchantments.Enchantment
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.potion.PotionType
import org.junit.jupiter.api.Test

class SerializerSchemaTest {
    @Serializable
    class Sample(
        val spread: @Serializable(IntRangeSerializer::class) IntRange,
        val chance: @Serializable(DoubleRangeSerializer::class) DoubleRange,
        val effects: List<@Serializable(PotionEffectSerializer::class) PotionEffect>,
        val effectType: @Serializable(PotionEffectTypeSerializer::class) PotionEffectType,
        val potion: @Serializable(PotionTypeSerializer::class) PotionType,
        val attribute: @Serializable(AttributeSerializer::class) Attribute,
        val enchantment: @Serializable(EnchantmentSerializer::class) Enchantment,
        val material: @Serializable(MaterialByNameSerializer::class) Material,
        val flag: @Serializable(TriStateSerializer::class) TriState,
        val lore: @Serializable(StringsSerializer::class) List<String>,
        val item: SerializableItemStack,
    )

    // Runs without a server, so building any of these must not touch Bukkit registries
    private val schema = jsonSchema {
        val scanned = SchemaDescriptorScanner.scan(javaClass.classLoader, listOf("com.mineinabyss.idofront"))
        val generator = KotlinxSerializationJsonSchemaGenerator(schemaDescriptors = { scanned[it.serialName] })
        generator.provideRequestedDefinitions()
        rootProperty { generator.applyClassDescriptor(Sample.serializer().descriptor) }
    }

    private fun property(name: String): JsonObject {
        val sample = schema.def(schema["\$ref"]!!.jsonPrimitive.content)
        return schema.resolve(sample["properties"]!!.jsonObject[name]!!.jsonObject)
    }

    private fun JsonObject.def(ref: String) = this["\$defs"]!!.jsonObject[ref.removePrefix("#/\$defs/")]!!.jsonObject

    private fun JsonObject.resolve(property: JsonObject): JsonObject =
        property["\$ref"]?.let { resolve(def(it.jsonPrimitive.content)) } ?: property

    private fun JsonObject.anyOf(): List<JsonObject> = this["anyOf"]!!.jsonArray.map { schema.resolve(it.jsonObject) }

    private fun JsonObject.types() = anyOf().mapNotNull { it["type"]?.jsonPrimitive?.content }

    private fun JsonElement.strings() = jsonArray.map { it.jsonPrimitive.content }

    @Test
    fun `number ranges accept numbers and range strings`() {
        property("spread").types() shouldContainAll listOf("integer", "string")
        property("chance").types() shouldContainAll listOf("number", "string")
        val pattern = Regex(property("spread").anyOf().first { it["type"]?.jsonPrimitive?.content == "string" }["pattern"]!!.jsonPrimitive.content)
        listOf("5", "1-3", "1..3", "-2..4", "1 to 3", "-3").forEach { pattern.containsMatchIn(it) shouldBe true }
        listOf("1-", "-1-3", "a").forEach { pattern.containsMatchIn(it) shouldBe false }
    }

    @Test
    fun `registry entries suggest vanilla keys and accept any namespaced key`() {
        val effectType = property("effectType").anyOf()
        effectType[0]["enum"]!!.strings() shouldContain "minecraft:night_vision"
        effectType[1]["pattern"]!!.jsonPrimitive.content shouldBe "^[a-z0-9_.-]+:[a-z0-9_./-]+$"
        property("potion").anyOf()[0]["enum"]!!.strings() shouldContain "minecraft:long_swiftness"
        property("attribute").anyOf()[0]["enum"]!!.strings() shouldContain "minecraft:max_health"
        property("enchantment").anyOf()[0]["enum"]!!.strings() shouldContain "minecraft:sharpness"
        property("material").anyOf()[0]["enum"]!!.strings() shouldContainAll listOf("minecraft:diamond", "minecraft:water")
    }

    @Test
    fun `shorthand or full forms are both accepted`() {
        property("flag").types() shouldContain "boolean"
        property("flag").anyOf()[1]["enum"]!!.strings() shouldContain "not_set"
        property("lore").types() shouldContainAll listOf("string", "array")
        property("item").anyOf().first()["type"]!!.jsonPrimitive.content shouldBe "string"
    }
}
