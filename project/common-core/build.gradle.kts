taboolib {
    version {
        skipKotlinRelocate = true
    }
    subproject = true
}

dependencies {
    val kotlinVersion = "2.3.0"
    compileOnly(project(":project:common"))
    compileOnly("org.jetbrains.kotlin:kotlin-main-kts:$kotlinVersion")
    compileOnly("org.jetbrains.kotlin:kotlin-scripting-common:$kotlinVersion")
    compileOnly("org.jetbrains.kotlin:kotlin-scripting-jvm:$kotlinVersion")
    compileOnly("org.jetbrains.kotlin:kotlin-scripting-jvm-host:$kotlinVersion")
    compileOnly("org.jetbrains.kotlin:kotlin-scripting-compiler-embeddable:$kotlinVersion")
    compileOnly("org.jetbrains.kotlin:kotlin-scripting-compiler-impl-embeddable:$kotlinVersion")
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
}

//tasks {
//    withType<ShadowJar> {
//        archiveClassifier.set("")
//        relocate("taboolib", "ink.ptms.artifex.taboolib")
////        dependencies {
////            val kotlinVersion = "1.8.22"
////            include(dependency("org.jetbrains.intellij.deps:trove4j:1.0.20181211"))
////            include(dependency("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"))
////            include(dependency("org.jetbrains.kotlin:kotlin-stdlib-common:$kotlinVersion"))
////            include(dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion"))
////            include(dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion"))
////            include(dependency("org.jetbrains.kotlin:kotlin-compiler-embeddable:$kotlinVersion"))
////            include(dependency("org.jetbrains.kotlin:kotlin-daemon-embeddable:$kotlinVersion"))
////            include(dependency("org.jetbrains.kotlin:kotlin-main-kts:$kotlinVersion"))
////            include(dependency("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion"))
////            include(dependency("org.jetbrains.kotlin:kotlin-script-runtime:$kotlinVersion"))
////            include(dependency("org.jetbrains.kotlin:kotlin-scripting-common:$kotlinVersion"))
////            include(dependency("org.jetbrains.kotlin:kotlin-scripting-compiler-embeddable:$kotlinVersion"))
////            include(dependency("org.jetbrains.kotlin:kotlin-scripting-compiler-impl-embeddable:$kotlinVersion"))
////            include(dependency("org.jetbrains.kotlin:kotlin-scripting-jvm:$kotlinVersion"))
////            include(dependency("org.jetbrains.kotlin:kotlin-scripting-jvm-host:$kotlinVersion"))
////            include(dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.8"))
////        }
//    }
//    build {
//        dependsOn(shadowJar)
//    }
//}