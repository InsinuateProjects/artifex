val taboolib_version: String by project

plugins {
    id("io.izzel.taboolib") version "1.40"
}

taboolib {
    install("common")
    install("module-configuration")
    options("skip-taboolib-relocate")
    classifier = null
    version = taboolib_version
    exclude("taboolib")
    relocate("io.github.lukehutch", "taboolib.library")
}

dependencies {
    api(project(":project:common"))
    compileOnly("io.github:fast-classpath-scanner:3.1.13")
}