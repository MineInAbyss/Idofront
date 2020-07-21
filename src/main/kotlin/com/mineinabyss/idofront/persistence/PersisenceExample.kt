package com.mineinabyss.idofront.persistence

import org.bukkit.entity.Player

//TODO move into wiki
data class SomeInfo(
        val player: Player,
        private val _duration: Int,
        private val _name: String
) : PersistentComponent {
    override val persistDelegateInfo = PersistDelegateInfo(player.persistentDataContainer, TODO("Send plugin here"))
    var duration by persistent<Int>() defaultTo _duration
    var name by persistent<String>() defaultTo _name
}
