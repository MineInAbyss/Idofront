package com.mineinabyss.idofront.nms.aliases

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.*
import net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball
import org.bukkit.entity.Creature

typealias NMSEntity = Entity
typealias NMSLivingEntity = LivingEntity
typealias NMSMob = Mob
typealias NMSPathfinderMob = PathfinderMob
typealias NMSCreature = Creature
typealias NMSPlayer = ServerPlayer
typealias NMSSnowball = Snowball

typealias NMSEntityType<T> = EntityType<T>
