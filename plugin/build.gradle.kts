import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer
import io.izzel.taboolib.gradle.App
import java.util.zip.ZipOutputStream
import java.io.FileOutputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.zip.Deflater
import java.util.zip.ZipFile

plugins {
    java
    id("com.github.johnrengelman.shadow")
}

taboolib {
    description {
        name(rootProject.name)
        contributors {
            name("坏黑")
        }
    }
}

dependencies {
//    taboo(project(":project:common"))
//    taboo(project(":project:common-script-api"))
//    taboo(project(":project:common-script-api-bukkit"))
//    taboo(project(":project:common-script-api-bungee"))
//    // 运行平台
//    taboo(project(":project:bootstrap-bukkit"))
//    taboo(project(":project:bootstrap-bungee"))
//    taboo(project(":project:bootstrap-velocity"))
//    // 逻辑实现
//    taboo(project(":project:common-impl-default"))
//    taboo(project(":project:common-impl-project"))
    // 所有模块应当已构建完成, 预先预热, 确保该模块在队列的末尾
    rootProject.subprojects.filter { it.path.startsWith(":project:") }.forEach {
        compileOnly(project(it.path))
    }
    // 第三方库
    taboo("ink.ptms:um:1.0.0-beta-29")
    taboo("io.github:fast-classpath-scanner:3.1.13")
//    taboo("me.lucko:jar-relocator:1.5")
}

tasks {
//    withType<ShadowJar> {
//        archiveClassifier.set("")
//        // 移除不必要的文件
//        exclude("META-INF/maven/**")
//        exclude("META-INF/tf/**")
//        exclude("module-info.java")
//        // 重定向 kotlin
//        relocate("kotlin.", "kotlin1822.") {
//            exclude("kotlin.Metadata")
//        }
//        // 重定向 TabooLib
//        relocate("taboolib", "ink.ptms.artifex.taboolib")
//        // 第三方库
//        relocate("ink.ptms.um", "ink.ptms.artifex.library.um")
//        relocate("io.github.lukehutch", "ink.ptms.artifex.library")
//        // asm
//        relocate("org.objectweb.asm.", "org.objectweb.asm9.")
//    }

    jar {
        // 打包相关子项目源代码
        rootProject.subprojects
            .filter {
                !it.name.startsWith("jar-")
                        && !it.name.startsWith("common-core")
                        && !it.name.startsWith("bootstrap-application")
            }
            .forEach {
                from(it.sourceSets["main"].output)
            }
        exclude("META-INF/maven/**")
        exclude("META-INF/tf/**")
        exclude("module-info.java")
//    exclude("kotlin.Metadata")
    }

    taboolibMainTask {
        dependsOn(jar)
    }

    processResources {
        rootProject.subprojects.filter { it.path.startsWith(":project:") }.forEach {
            dependsOn("${it.path}:shadowJar")
            dependsOn("${it.path}:jar")
        }
        // 运行环境及标准库
        intoZip(version, "runtime/core", "common-core")
        intoZip(version, "runtime/core-reflex", "common-core-reflex")
        intoZip(version, "runtime/script-api", "common-script-api")
        intoZip(version, "runtime/script-api-bukkit", "common-script-api-bukkit")
        intoZip(version, "runtime/script-api-bungee", "common-script-api-bungee")
        intoZip(version, "runtime/script-api-velocity", "common-script-api-velocity")
        // jar 代理
        intoZip(version, "proxy/bukkit", "jar-proxy-bukkit")
        intoZip(version, "proxy/bungee", "jar-proxy-bungee")
        intoZip(version, "proxy/velocity", "jar-proxy-velocity")
    }

    register<ShadowJar>("pluginJar") {
        rootProject.subprojects.filter { it.path.startsWith(":project:") }.forEach {
            dependsOn("${it.path}:shadowJar")
        }
        dependsOn(taboolibMainTask)
        dependencies {
            exclude(dependency("*:*"))
        }
        from(jar)
        archiveBaseName.set(rootProject.name)
        archiveClassifier.set("")
    }

    register<ShadowJar>("appJar") {
        dependsOn("pluginJar")
        dependencies {
            exclude(dependency("*:*"))
        }
        from(projectDir.resolve("build/libs/${rootProject.name}-$version.jar")) {
            exclude("META-INF")
        }
        manifest {
            attributes(mapOf(
                "Main-Class" to "ink.ptms.artifex.appside.Main"
            ))
        }
        archiveBaseName.set(rootProject.name)
        archiveClassifier.set("app")
        from(zipTree(rootProject.file("project/bootstrap-application/build/libs/bootstrap-application-$version.jar")))
        transform(Log4j2PluginsCacheFileTransformer())
        mergeServiceFiles()
    }

    register<ShadowJar>("apiJar") {
        archiveBaseName.set(rootProject.name)
        archiveClassifier.set("api")
        dependencies {
            exclude(dependency("*:*"))
        }
        from(project(":project:common").sourceSets["main"].output)
        from(project(":project:common-core").sourceSets["main"].output)
        from(project(":project:common-impl-default").sourceSets["main"].output)
        from(project(":project:common-impl-project").sourceSets["main"].output)
        from(project(":project:common-script-api").sourceSets["main"].output)
        from(project(":project:common-script-api-bukkit").sourceSets["main"].output)
        from(project(":project:common-script-api-bungee").sourceSets["main"].output)
        from(project(":project:common-script-api-velocity").sourceSets["main"].output)
    }

    register<ShadowJar>("sourceJar") {
        archiveBaseName.set(rootProject.name)
        archiveClassifier.set("sources")
        dependencies {
            exclude(dependency("*:*"))
        }
        from(project(":project:common").sourceSets["main"].allSource)
        from(project(":project:common-core").sourceSets["main"].allSource)
        from(project(":project:common-impl-default").sourceSets["main"].allSource)
        from(project(":project:common-impl-project").sourceSets["main"].allSource)
        from(project(":project:common-script-api").sourceSets["main"].allSource)
        from(project(":project:common-script-api-bukkit").sourceSets["main"].allSource)
        from(project(":project:common-script-api-bungee").sourceSets["main"].allSource)
        from(project(":project:common-script-api-velocity").sourceSets["main"].allSource)
    }

    build {
        dependsOn("pluginJar", "appJar", "apiJar", "sourceJar")
        /*doLast {
            val version = project.version
            val file = projectDir.resolve("build/libs/plugin-$version.jar")
            val pluginFile = projectDir.resolve("build/libs/${rootProject.name.lowercase()}-$version.jar")
            ZipFile(file).use { old ->
                ZipOutputStream(FileOutputStream(pluginFile)).use { new ->
                    for (entry in old.entries()) {
                        runCatching {
                            new.putNextEntry(entry)
                        }
                        if (!entry.isDirectory) {
                            new.write(old.getInputStream(entry).readBytes())
                        }
                        new.closeEntry()
                    }

                    // 因为 TabooLib 运行在 relocated 后的 Kotlin 环境中 (kotlin1822)
                    // 因此需要给脚本提供未经重定向的 jar 文件来进行编译

                    // 运行环境及标准库
                    applyToZip(new, version, "runtime/core", "common-core")
                    applyToZip(new, version, "runtime/core-reflex", "common-core-reflex")
                    applyToZip(new, version, "runtime/script-api", "common-script-api")
                    applyToZip(new, version, "runtime/script-api-bukkit", "common-script-api-bukkit")
                    applyToZip(new, version, "runtime/script-api-bungee", "common-script-api-bungee")
                    applyToZip(new, version, "runtime/script-api-velocity", "common-script-api-velocity")
                    // jar 代理
                    applyToZip(new, version, "proxy/bukkit", "jar-proxy-bukkit")
                    applyToZip(new, version, "proxy/bungee", "jar-proxy-bungee")
                    applyToZip(new, version, "proxy/velocity", "jar-proxy-velocity")
                }
            }

            // 用于独立运行的版本
            val appFile = projectDir.resolve("build/libs/${rootProject.name.lowercase()}-app-$version.jar")
            ZipFile(pluginFile).use { old ->
                ZipOutputStream(FileOutputStream(appFile)).use { new ->
                    applyToZipFully(new, version, false, "bootstrap-application")
                    for (entry in old.entries()) {
                        if (entry.name == "plugin.yml") continue
                        if (entry.name == "bungee.yml") continue
                        if (entry.name == "velocity-plugin.json") continue
                        runCatching {
                            new.putNextEntry(entry)
                            if (!entry.isDirectory) {
                                new.write(old.getInputStream(entry).readBytes())
                            }
                            new.closeEntry()
                        }
                    }
                }
            }

            // 提供用于开发的未经重定向的版本
            val newFile = projectDir.resolve("build/libs/${rootProject.name.lowercase()}-api-$version.jar")
            ZipOutputStream(FileOutputStream(newFile)).use { new ->
                applyToZipFully(new, version, true, "common")
                applyToZipFully(new, version, true, "common-core")
                applyToZipFully(new, version, true, "common-impl-default")
                applyToZipFully(new, version, true, "common-impl-project")
                applyToZipFully(new, version, true, "common-script-api")
                applyToZipFully(new, version, true, "common-script-api-bukkit")
                applyToZipFully(new, version, true, "common-script-api-bungee")
                applyToZipFully(new, version, true, "common-script-api-velocity")
            }
        }*/
    }
}

fun ProcessResources.intoZip(version: Any, name: String, module: String) {
    val directory = name.substringBeforeLast('/')
    val jarName = name.substringAfterLast('/')
    from(rootProject.file("project/$module/build/libs/$module-$version-relocated.jar")) {
        rename("$module-$version-relocated.jar", "$jarName.jar")
        into(directory)
    }
}