taboolib {
    version {
        skipKotlinRelocate = true
    }
    env {
    }
    subproject = true
}

dependencies {
    api(project(":project:common"))
    compileOnly("com.hypixel:hytale-server:1.0.0")
}