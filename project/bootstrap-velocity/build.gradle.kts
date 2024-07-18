taboolib {
    version {
        skipKotlinRelocate = true
    }
    subproject = true
}

dependencies {
    api(project(":project:common"))
    compileOnly("io.papermc:velocity:3.3.0:376")
}