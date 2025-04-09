import io.izzel.taboolib.gradle.App

taboolib {
    env {
        modules.removeIf { it.startsWith("platform-") }
        install(App)
    }
//    version {
//        skipKotlinRelocate = true
//    }
}

dependencies {
    api(project(":project:common"))

//    taboo(kotlin("stdlib"))

    taboo("org.jline:jline-terminal:3.21.0")
    taboo("org.jline:jline-terminal-jna:3.21.0")
    taboo("org.jline:jline-reader:3.21.0")

    taboo("org.apache.logging.log4j:log4j-api:2.20.0")
    taboo("org.apache.logging.log4j:log4j-core:2.20.0")
    taboo("org.apache.logging.log4j:log4j-slf4j2-impl:2.20.0")
    taboo("org.apache.logging.log4j:log4j-iostreams:2.20.0")
    taboo("org.apache.logging.log4j:log4j-jul:2.20.0")
}