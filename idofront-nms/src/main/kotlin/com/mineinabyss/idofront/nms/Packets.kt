package com.mineinabyss.idofront.nms

import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import io.papermc.paper.network.ChannelInitializeListenerHolder
import it.unimi.dsi.fastutil.ints.IntList
import net.kyori.adventure.key.Key
import net.minecraft.network.Connection
import net.minecraft.network.protocol.Packet
import net.minecraft.resources.Identifier
import net.minecraft.tags.TagNetworkSerialization.NetworkPayload
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

/**
 * Intercept a Clientbound packet to either alter it or prevent it from being sent to the player
 * @param key Optional key to register the packet-listener with
 * @param intercept A lambda function that takes two parameters:
 *  * `packet` (Packet<*>): The intercepted packet.
 *  * `player` (Player?): The player associated with the intercepted packet, or `null` if the player is not available.
 *  * The lambda should return the modified packet or `null` to prevent it from being sent.
 * @return The modified packet or null to prevent it from sending
 */
@JvmName("interceptClientboundPlayer")
fun JavaPlugin.interceptClientbound(key: String = "write_packet_interceptor", intercept: (Packet<*>, Player?) -> Packet<*>?) {
    PacketListener.interceptClientbound(Key.key(this.name.lowercase(), key.substringAfter(":")), intercept)
}

@JvmName("interceptClientboundConnection")
fun JavaPlugin.interceptClientbound(key: String = "write_packet_interceptor", intercept: (Packet<*>, Connection) -> Packet<*>?) {
    PacketListener.interceptClientbound(Key.key(this.name.lowercase(), key.substringAfter(":")), intercept)
}

/**
 * Intercept a Clientbound packet to either alter it or prevent it from being sent to the player
 * @param key Optional key to register the packet-listener with
 * @param intercept A lambda function that takes two parameters:
 *  * `packet` (Packet<*>): The intercepted packet.
 *  * `player` (Player?): The player associated with the intercepted packet, or `null` if the player is not available.
 *  * The lambda should return the modified packet or `null` to prevent it from being sent.
 * @return The modified packet or null to prevent it from sending
 */
@JvmName("interceptServerboundPlayer")
fun JavaPlugin.interceptServerbound(key: String = "read_packet_interceptor", intercept: (Packet<*>, Player?) -> Packet<*>?) {
    PacketListener.interceptServerbound(Key.key(this.name.lowercase(), key.substringAfter(":")), intercept)
}

@JvmName("interceptServerboundConnection")
fun JavaPlugin.interceptServerbound(key: String = "read_packet_interceptor", intercept: (Packet<*>, Connection) -> Packet<*>?) {
    PacketListener.interceptServerbound(Key.key(this.name.lowercase(), key.substringAfter(":")), intercept)
}

private val networkPayloadTagsField = NetworkPayload::class.java.getDeclaredField("tags").also { it.isAccessible = true }
private val networkPayloadConstructor = NetworkPayload::class.java.declaredConstructors.first().also { it.isAccessible = true }
fun NetworkPayload.tags() = (networkPayloadTagsField.get(this) as Map<Identifier, IntList>).toMutableMap()
fun Map<Identifier, IntList>.networkPayload() = networkPayloadConstructor.newInstance(this) as NetworkPayload

object PacketListener {

    /**
     * Intercept a Clientbound packet to either alter it or prevent it from being sent to the player
     * @param key Optional key to register the packet-listener with
     * @param intercept A lambda function that takes two parameters:
     *  * `packet` (Packet<*>): The intercepted packet.
     *  * `player` (Player?): The player associated with the intercepted packet, or `null` if the player is not available.
     *  * The lambda should return the modified packet or `null` to prevent it from being sent.
     * @return The modified packet or null to prevent it from sending
     */
    @JvmName("interceptClientboundPlayer")
    fun interceptClientbound(plugin: JavaPlugin, key: String = "write_packet_interceptor", intercept: (Packet<*>, Player?) -> Packet<*>?) {
        interceptClientbound(Key.key(plugin.name.lowercase(), key.substringAfter(":")), intercept)
    }

    @JvmName("interceptClientboundConnection")
    fun interceptClientbound(plugin: JavaPlugin, key: String = "write_packet_interceptor", intercept: (Packet<*>, Connection) -> Packet<*>?) {
        interceptClientbound(Key.key(plugin.name.lowercase(), key.substringAfter(":")), intercept)
    }

    /**
     * Intercept a Clientbound packet to either alter it or prevent it from being sent to the player
     * @param key Optional key to register the packet-listener with
     * @param intercept A lambda function that takes two parameters:
     *  * `packet` (Packet<*>): The intercepted packet.
     *  * `player` (Player?): The player associated with the intercepted packet, or `null` if the player is not available.
     *  * The lambda should return the modified packet or `null` to prevent it from being sent.
     * @return The modified packet or null to prevent it from sending
     */
    @JvmName("interceptServerboundPlayer")
    fun interceptServerbound(plugin: JavaPlugin, key: String = "read_packet_interceptor", intercept: (Packet<*>, Player?) -> Packet<*>?) {
        interceptServerbound(Key.key(plugin.name.lowercase(), key.substringAfter(":")), intercept)
    }

    @JvmName("interceptServerboundConnection")
    fun interceptServerbound(plugin: JavaPlugin, key: String = "read_packet_interceptor", interceptConnection: (Packet<*>, Connection) -> Packet<*>?) {
        interceptServerbound(Key.key(plugin.name.lowercase(), key.substringAfter(":")), interceptConnection)
    }

    fun unregisterListener(plugin: JavaPlugin) {
        ChannelInitializeListenerHolder.getListeners().keys.filter { it.namespace() == plugin.name.lowercase() }.forEach(ChannelInitializeListenerHolder::removeListener)
    }

    fun unregisterListener(key: Key) {
        ChannelInitializeListenerHolder.removeListener(key)
    }

    @JvmName("interceptClientboundPlayer")
    internal fun interceptClientbound(key: Key? = null, intercept: (Packet<*>, Player?) -> Packet<*>?) {
        val key = key ?: Key.key("write_packet_interceptor${ChannelInitializeListenerHolder.getListeners().keys.indexOfLast { it.value().startsWith("write_packet_interceptor") }.plus(1)}")

        ChannelInitializeListenerHolder.addListener(key) { channel ->
            channel.pipeline().addBefore("packet_handler", key.asString(), object : ChannelDuplexHandler() {
                val connection = channel.pipeline()["packet_handler"] as Connection
                override fun write(ctx: ChannelHandlerContext, packet: Any, promise: ChannelPromise) {
                    // Player IS nullable, just not marked in Paper
                    if (packet is Packet<*>) intercept(packet, connection.player?.bukkitEntity)?.let { ctx.write(it, promise) }
                    else ctx.write(packet, promise)
                }
            })
        }
    }

    @JvmName("interceptClientboundConnection")
    internal fun interceptClientbound(key: Key? = null, intercept: (Packet<*>, Connection) -> Packet<*>?) {
        val key = key ?: Key.key("write_packet_interceptor${ChannelInitializeListenerHolder.getListeners().keys.indexOfLast { it.value().startsWith("write_packet_interceptor") }.plus(1)}")

        ChannelInitializeListenerHolder.addListener(key) { channel ->
            channel.pipeline().addBefore("packet_handler", key.asString(), object : ChannelDuplexHandler() {
                val connection = channel.pipeline()["packet_handler"] as Connection
                override fun write(ctx: ChannelHandlerContext, packet: Any, promise: ChannelPromise) {
                    // Player IS nullable, just not marked in Paper
                    if (packet is Packet<*>) intercept(packet, connection)?.let { ctx.write(it, promise) }
                    else ctx.write(packet, promise)
                }
            })
        }
    }

    @JvmName("interceptServerboundPlayer")
    internal fun interceptServerbound(key: Key? = null, intercept: (Packet<*>, Player?) -> Packet<*>?) {
        val key = key ?: Key.key("write_packet_interceptor${ChannelInitializeListenerHolder.getListeners().keys.indexOfLast { it.value().startsWith("read_packet_interceptor") }.plus(1)}")

        ChannelInitializeListenerHolder.addListener(key) { channel ->
            channel.pipeline().addBefore("packet_handler", key.asString(), object : ChannelDuplexHandler() {
                val connection = channel.pipeline()["packet_handler"] as Connection
                override fun channelRead(ctx: ChannelHandlerContext, packet: Any) {
                    // Player IS nullable, just not marked in Paper
                    if (packet is Packet<*>) intercept(packet, connection.player?.bukkitEntity)?.let { ctx.fireChannelRead(it) }
                    else ctx.fireChannelRead(packet)
                }
            })
        }
    }

    @JvmName("interceptServerboundConnection")
    internal fun interceptServerbound(key: Key? = null, intercept: (Packet<*>, Connection) -> Packet<*>?) {
        val key = key ?: Key.key("write_packet_interceptor${ChannelInitializeListenerHolder.getListeners().keys.indexOfLast { it.value().startsWith("read_packet_interceptor") }.plus(1)}")

        ChannelInitializeListenerHolder.addListener(key) { channel ->
            channel.pipeline().addBefore("packet_handler", key.asString(), object : ChannelDuplexHandler() {
                val connection = channel.pipeline()["packet_handler"] as Connection
                override fun channelRead(ctx: ChannelHandlerContext, packet: Any) {
                    // Player IS nullable, just not marked in Paper
                    if (packet is Packet<*>) intercept(packet, connection)?.let { ctx.fireChannelRead(it) }
                    else ctx.fireChannelRead(packet)
                }
            })
        }
    }
}
