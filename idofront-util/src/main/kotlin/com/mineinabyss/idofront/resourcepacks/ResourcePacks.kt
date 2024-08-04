package com.mineinabyss.idofront.resourcepacks

import team.unnamed.creative.serialize.minecraft.MinecraftResourcePackReader
import team.unnamed.creative.serialize.minecraft.MinecraftResourcePackWriter

object ResourcePacks {
    val resourcePackWriter = MinecraftResourcePackWriter.builder().prettyPrinting(true).build()
    val resourcePackReader = MinecraftResourcePackReader.builder().lenient(true).build()
}