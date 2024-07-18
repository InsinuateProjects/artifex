import io.izzel.taboolib.gradle.*

val taboolib_version: String by project

plugins {
    id("org.gradle.java")
    id("org.gradle.maven-publish")
    id("org.jetbrains.kotlin.jvm") version "1.8.20"
    id("io.izzel.taboolib") version "2.0.13"
}

subprojects {
    apply(plugin = "io.izzel.taboolib")
    apply<JavaPlugin>()
    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        mavenLocal()
        mavenCentral()
        maven { url = uri("https://repo.tabooproject.org/repository/releases") }
    }

    taboolib {
        env {
            install(
                CONFIGURATION,
                CHAT,
                LANG,
                KETHER,
                EFFECT,
                DATABASE
            )
            install(
                EXPANSION_COMMAND_HELPER,
                EXPANSION_PLAYER_DATABASE,
                EXPANSION_REDIS, EXPANSION_LETTUCE_REDIS,
                EXPANSION_JAVASCRIPT,
                EXPANSION_GEEK_TOOL,
                EXPANSION_PLAYER_FAKE_OP,
                EXPANSION_PTC, EXPANSION_PTC_OBJECT,
                EXPANSION_SUBMIT_CHAIN
            )
            install(BUKKIT_ALL, UI, NMS, NMS_UTIL)
            install(BUNGEE, PORTICUS)
            install(VELOCITY)
        }
        version {
            taboolib = taboolib_version
            coroutines = null
        }
        classifier = null
    }

    dependencies {
        "compileOnly"(kotlin("stdlib"))
        // Reflex Remapper
        "compileOnly"("org.ow2.asm:asm:9.6")
        "compileOnly"("org.ow2.asm:asm-util:9.6")
        "compileOnly"("org.ow2.asm:asm-commons:9.6")
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-Xjvm-default=all")
        }
    }

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

publishing {
    repositories {
        maven {
            url = uri("https://repo.tabooproject.org/repository/releases")
            credentials {
                username = project.findProperty("taboolibUsername").toString()
                password = project.findProperty("taboolibPassword").toString()
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("library") {
            from(components["java"])
            groupId = project.group.toString()
        }
    }
}

gradle.buildFinished {
    buildDir.deleteRecursively()
}