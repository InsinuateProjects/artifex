import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.util.zip.ZipOutputStream
import java.io.FileOutputStream
import java.util.jar.JarEntry
import java.util.zip.ZipFile

plugins {
    java
    id("com.github.johnrengelman.shadow")
}

dependencies {
    implementation(project(":project:common"))
    implementation(project(":project:common-bridge"))
    implementation(project(":project:controller"))
    implementation(project(":project:implementation-bukkit"))
    implementation(project(":project:implementation-bungee"))
    implementation(project(":project:implementation-common-default"))
    implementation(project(":project:implementation-common-project"))
}

tasks {
    withType<ShadowJar> {
        archiveClassifier.set("")
        exclude("META-INF/maven/**")
        exclude("META-INF/tf/**")
        exclude("module-info.java")
        relocate("taboolib.platform.type.BukkitProxyEvent", "ink.ptms.artifex.taboolib.platform.type.BukkitProxyEvent")
        relocate("taboolib.platform.type.BungeeProxyEvent", "ink.ptms.artifex.taboolib.platform.type.BungeeProxyEvent")
    }
    create("collect") {
        doFirst {
            val version = project.version
            val file = projectDir.resolve("build/libs/plugin-$version.jar")
            val newFile = projectDir.resolve("build/libs/${rootProject.name}-$version.jar")
            ZipFile(file).use { old ->
                ZipOutputStream(FileOutputStream(newFile)).use { new ->
                    for (entry in old.entries()) {
                        new.putNextEntry(entry)
                        if (!entry.isDirectory) {
                            new.write(old.getInputStream(entry).readBytes())
                        }
                        new.closeEntry()
                    }
                    new.putNextEntry(JarEntry("runtime/bridge.jar"))
                    new.write(rootProject.file("project/common-bridge/build/libs/common-bridge-$version-origin.jar").readBytes())
                    new.closeEntry()
                    new.putNextEntry(JarEntry("runtime/core.jar"))
                    new.write(rootProject.file("project/common-runtime/build/libs/common-runtime-$version.jar").readBytes())
                    new.closeEntry()
                    new.putNextEntry(JarEntry("proxy/bukkit.jar"))
                    new.write(rootProject.file("project/proxy-bukkit/build/libs/proxy-bukkit-$version.jar").readBytes())
                    new.closeEntry()
                    new.putNextEntry(JarEntry("proxy/bungee.jar"))
                    new.write(rootProject.file("project/proxy-bungee/build/libs/proxy-bungee-$version.jar").readBytes())
                    new.closeEntry()
                }
            }
        }
    }
    build {
        dependsOn(shadowJar)
    }
}