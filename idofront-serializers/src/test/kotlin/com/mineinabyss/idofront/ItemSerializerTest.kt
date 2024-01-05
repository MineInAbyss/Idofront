package com.mineinabyss.idofront

import be.seeseemelk.mockbukkit.MockBukkit
import com.charleskorn.kaml.Yaml
import com.mineinabyss.idofront.serialization.SerializableItemStack
import com.mineinabyss.idofront.serialization.SerializableItemStackSerializer
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import org.bukkit.Material
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemSerializerTest {
    @BeforeAll
    fun setup() {
        MockBukkit.mock()
    }

    @Test
    fun `should serialize item as string`() {
        val input = """
        minecraft:stone
        """.trimIndent()

        Yaml().decodeFromString(SerializableItemStackSerializer(), input).type shouldBe Material.STONE
    }

    @Test
    fun `should serialize item as class`() {
        val input = """
        type: minecraft:stone
        """.trimIndent()

        Yaml().decodeFromString(SerializableItemStackSerializer(), input).type shouldBe Material.STONE
    }

    @Test
    fun `should serialize item as class when in list`() {
        val input = """
        - type: minecraft:stone
        """.trimIndent()

        Yaml().decodeFromString(ListSerializer(SerializableItemStackSerializer()), input).shouldContainExactly(
            SerializableItemStack(type = Material.STONE)
        )
    }

    @Test
    fun `should serialize item as class when in map`() {
        val input = """
            myKey:
                type: minecraft:stone
        """.trimIndent()

        Yaml().decodeFromString(MapSerializer(String.serializer(), SerializableItemStackSerializer()), input)
            .shouldContainExactly(
                mapOf("myKey" to SerializableItemStack(type = Material.STONE))
            )
    }

    @Test
    fun `should deserialize in JSON`() {
        val input = """
            [
                { "type": "minecraft:stone" }
            ]
        """.trimIndent()

        Json.decodeFromString(ListSerializer(SerializableItemStackSerializer()), input).shouldContainExactly(
            SerializableItemStack(type = Material.STONE)
        )
    }
}
