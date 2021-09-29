@Suppress("ClassName", "ObjectPropertyName")
object Deps {
    object minecraft {
        const val headlib = "de.erethon:headlib"
        const val skedule = "com.github.okkero:skedule"
    }

    object exposed {
        const val core = "org.jetbrains.exposed:exposed-core"
        const val dao = "org.jetbrains.exposed:exposed-dao"
        const val jdbc = "org.jetbrains.exposed:exposed-jdbc"
    }

    object kotlinx {
        const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-jdk8"

        object serialization {
            const val json = "org.jetbrains.kotlinx:kotlinx-serialization-json"
            const val cbor = "org.jetbrains.kotlinx:kotlinx-serialization-cbor"
            const val kaml = "com.charleskorn.kaml:kaml"
        }
    }

    const val `kotlin-statistics` = "org.nield:kotlin-statistics"
    const val `sqlite-jdbc` = "org.xerial:sqlite-jdbc"
}
