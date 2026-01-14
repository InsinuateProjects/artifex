taboolib {
    version {
        skipKotlinRelocate = true
    }
    subproject = true
}

dependencies {
    compileOnly(project(":project:common")) { isTransitive = false }
    compileOnly("com.hypixel:hytale-server:1.0.0")
}