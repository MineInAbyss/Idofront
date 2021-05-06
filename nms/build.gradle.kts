plugins {
    id("idofront.minecraft-conventions")
}

repositories {
    maven("https://repo.codemc.io/repository/nms/")
}

val serverVersion: String by project

dependencies {
    compileOnly("org.spigotmc:spigot:$serverVersion")
    api(project(":"))
}
