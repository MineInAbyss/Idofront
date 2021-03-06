@file:Suppress("NOTHING_TO_INLINE")

package com.mineinabyss.idofront.nms.pathfindergoals

import com.mineinabyss.idofront.nms.aliases.NMSEntityInsentient
import com.mineinabyss.idofront.nms.aliases.NMSPathfinderGoal
import com.mineinabyss.idofront.nms.aliases.NMSPathfinderGoalSelector
import net.minecraft.world.entity.ai.goal.PathfinderGoalSelector

inline fun NMSPathfinderGoalSelector.add(goal: NMSPathfinderGoal) = a(goal)
inline fun NMSPathfinderGoalSelector.add(priority: Int, goal: NMSPathfinderGoal) = a(priority, goal)

val NMSEntityInsentient.goalSelector: PathfinderGoalSelector get() = this.bP
val NMSEntityInsentient.targetSelector: PathfinderGoalSelector get() = this.bQ

fun NMSEntityInsentient.addPathfinderGoal(priority: Int, goal: NMSPathfinderGoal) = goalSelector.add(priority, goal)

fun NMSEntityInsentient.removePathfinderGoal(goal: NMSPathfinderGoal) = goalSelector.a(goal)

fun NMSEntityInsentient.addTargetSelector(priority: Int, goal: NMSPathfinderGoal) = targetSelector.add(priority, goal)

fun NMSEntityInsentient.removeTargetSelector(goal: NMSPathfinderGoal) = targetSelector.add(goal)
