package com.mineinabyss.idofront.services.impl

import com.mineinabyss.idofront.messaging.idofrontLogger
import com.mineinabyss.idofront.services.ItemProvider
import com.mineinabyss.idofront.services.SerializableItemStackService

class SerializableItemStackServiceImpl : SerializableItemStackService {
    val extensions = mutableMapOf<String, ItemProvider>()

    override fun registerProvider(prefix: String, provider: ItemProvider) {
        if (prefix.contains(" ")) {
            error("Cannot register item provider with space in prefix: $prefix")
        }

        // Print warning with strack trace to know which plugin is causing this issue.
        if (extensions.containsKey(prefix)) {
            idofrontLogger.w { IllegalStateException("An item provider with prefix '$prefix' was already registered").stackTraceToString() }
        } else {
            extensions[prefix] = provider
        }
    }

    override fun getProvider(prefix: String): ItemProvider? {
        return extensions[prefix]
    }
}
