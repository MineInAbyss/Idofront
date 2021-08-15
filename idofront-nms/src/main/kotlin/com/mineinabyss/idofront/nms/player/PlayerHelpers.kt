package com.mineinabyss.idofront.nms.player

import com.mineinabyss.idofront.nms.aliases.NMSDamageSource
import com.mineinabyss.idofront.nms.aliases.NMSEntity
import com.mineinabyss.idofront.nms.aliases.NMSEntityHuman

fun NMSEntityHuman.addKillScore(entity: NMSEntity, score: Int, damageSource: NMSDamageSource) {
    a(entity, score, damageSource)
}
