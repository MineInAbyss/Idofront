import org.gradle.kotlin.dsl.DependencyConstraintHandlerScope

fun DependencyConstraintHandlerScope.constrainKotlin(kotlinVersion: String) {
    fun enforce(name: String, version: String) {
        add("implementation", name) { version { strictly(version) } }
    }
    enforce("org.jetbrains.kotlin:kotlin-stdlib", kotlinVersion)
    enforce("org.jetbrains.kotlin:kotlin-stdlib-common", kotlinVersion)
    enforce("org.jetbrains.kotlin:kotlin-stdlib-jdk8", kotlinVersion)
    enforce("org.jetbrains.kotlin:kotlin-reflect", kotlinVersion)
}

// TODO exclude kotlin from runtime
//fun Project.test() {
//    configurations.apply {
//
//        "implementation" {
//            exclude(group = "org.jetbrains.kotlin")
//        }
//    }
//    configurations {
////        "implementation" {
////            exclude(group = "org.jetbrains.kotlin")
////        }
//    }
//}
