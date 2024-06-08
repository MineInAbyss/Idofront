package com.mineinabyss.idofront.nms

import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import io.papermc.paper.network.ChannelInitializeListenerHolder
import net.kyori.adventure.key.Key
import net.minecraft.network.Connection
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.server.network.ServerGamePacketListenerImpl
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
fun JavaPlugin.interceptClientbound(key: String = "write_packet_interceptor", intercept: (Packet<*>, Player?) -> Packet<*>?) {
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
fun JavaPlugin.interceptServerbound(key: String = "read_packet_interceptor", intercept: (Packet<*>, Player?) -> Packet<*>?) {
    PacketListener.interceptServerbound(Key.key(this.name.lowercase(), key.substringAfter(":")), intercept)
}

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
    fun interceptClientbound(plugin: JavaPlugin, key: String = "write_packet_interceptor", intercept: (Packet<*>, Player?) -> Packet<*>?) {
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
    fun interceptServerbound(plugin: JavaPlugin, key: String = "read_packet_interceptor", intercept: (Packet<*>, Player?) -> Packet<*>?) {
        interceptServerbound(Key.key(plugin.name.lowercase(), key.substringAfter(":")), intercept)
    }

    fun unregisterListener(plugin: JavaPlugin) {
        ChannelInitializeListenerHolder.getListeners().keys.filter { it.namespace() == plugin.name.lowercase() }.forEach(ChannelInitializeListenerHolder::removeListener)
    }

    fun unregisterListener(key: Key) {
        ChannelInitializeListenerHolder.removeListener(key)
    }

    internal fun interceptClientbound(key: Key? = null, intercept: (Packet<*>, Player?) -> Packet<*>?) {
        val key = key ?: Key.key("write_packet_interceptor${ChannelInitializeListenerHolder.getListeners().keys.indexOfLast { it.value().startsWith("write_packet_interceptor") }.plus(1)}")

        ChannelInitializeListenerHolder.addListener(key) { channel ->
            channel.pipeline().addBefore("packet_handler", key.asString(), object : ChannelDuplexHandler() {
                val connection = channel.pipeline()["packet_handler"] as Connection
                override fun write(ctx: ChannelHandlerContext, packet: Any, promise: ChannelPromise) {
                    val player = if (connection.packetListener is ClientGamePacketListener) connection.player.bukkitEntity else null
                    if (packet is Packet<*>) intercept(packet, player)?.let { ctx.write(it, promise) }
                    else ctx.write(packet, promise)
                }
            })
        }
    }

    internal fun interceptServerbound(key: Key? = null, intercept: (Packet<*>, Player?) -> Packet<*>?) {
        val key = key ?: Key.key("write_packet_interceptor${ChannelInitializeListenerHolder.getListeners().keys.indexOfLast { it.value().startsWith("read_packet_interceptor") }.plus(1)}")

        ChannelInitializeListenerHolder.addListener(key) { channel ->
            channel.pipeline().addBefore("packet_handler", key.asString(), object : ChannelDuplexHandler() {
                val connection = channel.pipeline()["packet_handler"] as Connection
                override fun channelRead(ctx: ChannelHandlerContext, packet: Any) {
                    val player = if (connection.packetListener is ServerGamePacketListenerImpl) connection.player.bukkitEntity else null
                    if (packet is Packet<*>) intercept(packet, player)?.let { ctx.fireChannelRead(it) }
                    else ctx.fireChannelRead(packet)
                }
            })
        }
    }
}
