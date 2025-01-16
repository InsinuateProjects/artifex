import io.izzel.taboolib.gradle.*

taboolib {
    version {
        skipKotlinRelocate = true
    }
    subproject = true
}

dependencies {
    api(project(":project:common"))
}