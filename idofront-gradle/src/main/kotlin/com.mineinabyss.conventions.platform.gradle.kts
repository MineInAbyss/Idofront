@Suppress("ClassName", "ObjectPropertyName")
object Deps {
    object minecraft {
        const val headlib = "com.github.DRE2N.HeadLib:headlib-core"
        const val skedule = "com.okkero:skedule"
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
        private val mia = "com.mineinabyss"
        val idofront = "$mia:idofront"
        val `idofront-nms` = "$mia:idofront-nms"

        val geary = "$mia:geary"
        val `geary-platform-papermc` = "$mia:geary-platform-papermc"
        val mobzy = "$mia:mobzy"
        val looty = "$mia:looty"
        val mineinabyss = "$mia:looty"
        val bonfire = "$mia:Bonfire"
        val protocolburrito = "$mia:protocolburrito"
        val bonehurtingjuice = "$mia:bonehurtingjuice"
    }

    object koin {
        private val koin = "io.insert-koin:koin"

        val core = "$koin-core"
        val test = "$koin-test"
        val `test-junit5` = "$koin-test-junit5"
        val ktor = "$koin-ktor"
    }
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
}
