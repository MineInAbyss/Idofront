plugins {
    `maven-publish`
}


publishing {
    addMineInAbyssRepo(project)

    // Conditional statement to support publishing on multiplatform
    if (components.findByName("java") != null) {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
            }
        }
    }
}
