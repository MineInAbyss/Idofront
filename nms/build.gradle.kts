repositories {
    maven("https://repo.codemc.io/repository/nms/")
}

dependencies {
    compileOnly("org.spigotmc:spigot:${IdofrontDeps.serverVersion}")
    api(project(":"))
}
