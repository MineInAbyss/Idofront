package com.mineinabyss.idofront.datastore

import com.mineinabyss.idofront.Idofront
import me.dvyy.sqlite.datastore.DataStoreLike

fun Idofront.setupDataStore(store: DataStoreLike) {
    db.launchWrite { store.table.create() }
}

