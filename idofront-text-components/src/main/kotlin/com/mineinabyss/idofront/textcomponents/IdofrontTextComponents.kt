package com.mineinabyss.idofront.textcomponents

import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

object IdofrontTextComponents {
    private val resolvers = mutableListOf(TagResolver.standard())
    var globalResolver = TagResolver.resolver(resolvers)
        private set

    fun addResolver(resolver: TagResolver) {
        resolvers.add(resolver)
        globalResolver = TagResolver.resolver(resolvers)
    }
}
