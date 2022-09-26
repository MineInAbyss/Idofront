package com.mineinabyss.idofront.textcomponents

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer

private val mm = MiniMessage.miniMessage()
private val plainComponentSerializer = PlainTextComponentSerializer.plainText()

/** Parses this String to a [Component] with MiniMessage and an optional TagResolver */
fun String.miniMsg(tagResolver: TagResolver = IdofrontTextComponents.globalResolver): Component =
    mm.deserialize(this, tagResolver)

/** Serializes this [Component] to a String with MiniMessage */
fun Component.serialize(): String = mm.serialize(this)

/** Serializes this [Component] to a plain text string */
fun Component.toPlainText(): String = plainComponentSerializer.serialize(this)

/** Removes all supported tags from a string, with an optional TagResolver input */
fun String.stripTags(tagResolver: TagResolver = IdofrontTextComponents.globalResolver): String =
    mm.stripTags(this, tagResolver)
