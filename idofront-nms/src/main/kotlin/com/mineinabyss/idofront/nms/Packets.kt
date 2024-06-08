package com.mineinabyss.idofront.nms

import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import io.papermc.paper.network.ChannelInitializeListenerHolder
import net.kyori.adventure.key.Key
import net.minecraft.network.Connection
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.ConcurrentHashMap

/**
 * Intercept a Clientbound packet to either alter it or prevent it from being sent to the player
 * @return The modified packet or null to prevent it from sending
 */
fun JavaPlugin.interceptClientbound(key: String = "write_packet_interceptor", intercept: (Packet<*>) -> Packet<*>?) {
    PacketListener.interceptClientbound(Key.key(this.name, key.substringAfter(":")), intercept)
}

/**
 * Intercept a Clientbound packet to either alter it or prevent it from being sent to the player
 * @return The modified packet or null to prevent it from sending
 */
fun JavaPlugin.interceptServerbound(key: String = "read_packet_interceptor", intercept: (Packet<*>) -> Packet<*>?) {
    PacketListener.interceptServerbound(Key.key(this.name, key.substringAfter(":")), intercept)
}

object PacketListener {
    val pluginListeners: MutableList<Key> = mutableListOf()

    /**
     * Intercept a Clientbound packet to either alter it or prevent it from being sent to the player
     * @return The modified packet or null to prevent it from sending
     */
    fun interceptClientbound(plugin: JavaPlugin, key: String = "write_packet_interceptor", intercept: (Packet<*>) -> Packet<*>?) {
        interceptClientbound(Key.key(plugin.name, key.substringAfter(":")), intercept)
    }

    /**
     * Intercept a Clientbound packet to either alter it or prevent it from being sent to the player
     * @return The modified packet or null to prevent it from sending
     */
    fun interceptServerbound(plugin: JavaPlugin, key: String = "read_packet_interceptor", intercept: (Packet<*>) -> Packet<*>?) {
        interceptServerbound(Key.key(plugin.name, key.substringAfter(":")), intercept)
    }

    fun unregisterListener(plugin: JavaPlugin) {
        pluginListeners.filter { it.namespace() == plugin.name }.forEach(ChannelInitializeListenerHolder::removeListener)
    }

    fun unregisterListener(key: Key) {
        ChannelInitializeListenerHolder.removeListener(key)
    }

    /**
     * Intercept a Clientbound packet to either alter it or prevent it from being sent to the player
     * @return The modified packet or null to prevent it from sending
     */
    internal fun interceptClientbound(key: Key? = null, intercept: (Packet<*>) -> Packet<*>?) {
        val key = key ?: Key.key("write_packet_interceptor${pluginListeners.filter { it.value().startsWith("write_packet_interceptor") }.size.minus(1)}")
        pluginListeners.add(key)

        ChannelInitializeListenerHolder.addListener(key) { channel ->
            channel.pipeline().addBefore("packet_handler", key.asString(), object : ChannelDuplexHandler() {
                override fun write(ctx: ChannelHandlerContext, packet: Any, promise: ChannelPromise) {
                    (packet as? Packet<*>)?.let { intercept(it)?.let { ctx.write(it, promise) } }
                }
            })
        }
    }

    /**
     * Intercepts a Serverbound packet to either alter it or prevent it from being sent to the server
     * @return The modified packet or null to prevent it from sending
     */
    internal fun interceptServerbound(key: Key? = null, intercept: (Packet<*>) -> Packet<*>?) {
        val key = key ?: Key.key("write_packet_interceptor${pluginListeners.filter { it.value().startsWith("write_packet_interceptor") }.size.minus(1)}")
        pluginListeners.add(key)

        ChannelInitializeListenerHolder.addListener(key) { channel ->
            channel.pipeline().addBefore("packet_handler", key.asString(), object : ChannelDuplexHandler() {
                override fun channelRead(ctx: ChannelHandlerContext, packet: Any) {
                    (packet as? Packet<*>)?.let { intercept(it)?.let { ctx.fireChannelRead(it) } }
                }
            })
        }
    }
}
