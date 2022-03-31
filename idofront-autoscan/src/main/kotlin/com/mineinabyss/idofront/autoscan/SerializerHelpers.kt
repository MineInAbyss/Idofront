package com.mineinabyss.idofront.autoscan

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.serializerOrNull
import kotlin.reflect.KClass


/** Gets all subclasses of type [T] on the classpath. */
@OptIn(InternalSerializationApi::class)
inline fun <reified T : Any> Collection<KClass<out T>>.polymorphicSerializer(): SerializersModule = SerializersModule {
    polymorphic(T::class) {
        filterIsInstance<KClass<T>>().forEach { kClass ->
            val serializer = kClass.serializerOrNull() ?: return@forEach
            subclass(kClass, serializer)
        }
    }
}
