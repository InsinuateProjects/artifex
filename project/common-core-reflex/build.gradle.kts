plugins {
    id("com.github.johnrengelman.shadow")
}

taboolib {
    version {
        skipKotlinRelocate = true
    }
    subproject = true
}

// 修复其它平台 asm 版本错误
dependencies {
    implementation("io.izzel.taboolib:common-reflex:${taboolib.version.taboolib}")
    implementation("org.ow2.asm:asm:9.6")
    implementation("org.ow2.asm:asm-util:9.6")
    implementation("org.ow2.asm:asm-commons:9.6")
}

tasks {
    shadowJar {
        dependencies {
            include(dependency("io.izzel.taboolib:common-reflex"))
            include(dependency("org.ow2.asm:asm:9.6"))
            include(dependency("org.ow2.asm:asm-util:9.6"))
            include(dependency("org.ow2.asm:asm-commons:9.6"))
        }
        archiveClassifier.set("relocated")
//        relocate("taboolib", "ink.ptms.artifex.taboolib")
        relocate("org.objectweb.asm.", "org.objectweb.asm9.")
    }
    build {
        dependsOn(shadowJar)
    }
}

tasks.named("taboolibMainTask") {
    dependsOn(tasks.named("shadowJar"))
}