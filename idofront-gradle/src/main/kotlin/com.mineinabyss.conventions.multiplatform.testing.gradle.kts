import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
}

val libs = idofrontLibsRef

repositories {
    mavenCentral()
    maven("https://repo.mineinabyss.com/releases")
}

kotlin {
    sourceSets {
        // Junit
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.findLibrary("kotest.assertions").get())
                implementation(libs.findLibrary("kotest.property").get())
            }
        }
    }
}

tasks {
    withType<KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }
}
