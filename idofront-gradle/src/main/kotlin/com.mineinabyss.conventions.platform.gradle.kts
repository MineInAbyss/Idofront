@Suppress("ClassName", "ObjectPropertyName")
object Deps {
    object minecraft {
        const val headlib = "com.github.DRE2N.HeadLib:headlib-core"
        const val skedule = "com.github.okkero:skedule"
        const val anvilgui = "net.wesjd:anvilgui"
    }

    object exposed {
        const val core = "org.jetbrains.exposed:exposed-core"
        const val dao = "org.jetbrains.exposed:exposed-dao"
        const val jdbc = "org.jetbrains.exposed:exposed-jdbc"
        const val `java-time` = "org.jetbrains.exposed:exposed-java-time"
    }

    object kotlin {
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
        const val reflect = "org.jetbrains.kotlin:kotlin-reflect"
    }

    object kotlinx {
        const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-jdk8"

        object serialization {
            const val json = "org.jetbrains.kotlinx:kotlinx-serialization-json"
            const val cbor = "org.jetbrains.kotlinx:kotlinx-serialization-cbor"
            const val hocon = "org.jetbrains.kotlinx:kotlinx-serialization-hocon"
            const val protobuf = "org.jetbrains.kotlinx:kotlinx-serialization-protobuf"
            const val properties = "org.jetbrains.kotlinx:kotlinx-serialization-properties"
            const val kaml = "com.charleskorn.kaml:kaml"
        }
    }

    const val `kotlin-statistics` = "org.nield:kotlin-statistics"
    const val `sqlite-jdbc` = "org.xerial:sqlite-jdbc"

    object mineinabyss {
        private val group = "com.mineinabyss"
        val idofront = "$group:idofront"
        val `idofront-nms` = "$group:idofront-nms"

        val geary = "$group:geary"
        val `geary-platform-papermc` = "$group:geary-platform-papermc"
        val mobzy = "$group:mobzy"
        val looty = "$group:looty"
        val mineinabyss = "$group:looty"
        val bonfire = "$group:Bonfire"
        val protocolburrito = "$group:protocolburrito"
        val bonehurtingjuice = "$group:bonehurtingjuice"
    }
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
}
