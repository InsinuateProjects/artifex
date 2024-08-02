package ink.ptms.artifex.script.impl

import taboolib.common.PrimitiveIO
import taboolib.common.PrimitiveSettings
import taboolib.common.env.*
import taboolib.common.io.newFile
import taboolib.common.platform.function.getDataFolder
import java.io.File
import java.io.FileNotFoundException
import java.net.URL

object KotlinEnvironments {

    /**
     * 获取 TabooLib 中的 env.properties 环境配置文件
     */
    val properties = PrimitiveSettings.RUNTIME_PROPERTIES

    /**
     * 默认下载源
     */
    val repository: String
        get() = "https://maven.aliyun.com/repository/central"

    /**
     * TabooLib 下载源
     */
    val repositoryTabooLib: String
        get() = "https://repo.tabooproject.org/repository/releases"


    private val baseDir = newFile(getDataFolder(), "runtime/libraries", folder = true)

    private val kotlinVersion = "1.8.20"

    private val relocation = listOf(JarRelocation("kotlin", "kotlin${kotlinVersion.replace(".", "")}"))

    fun loadDependencies() {
        loadDependencies("org.jetbrains.kotlin:kotlin-main-kts:$kotlinVersion", repository)
        loadDependencies("org.jetbrains.kotlin:kotlin-script-runtime:$kotlinVersion", repository)
        loadDependencies("org.jetbrains.kotlin:kotlin-scripting-common:$kotlinVersion", repository)
        loadDependencies("org.jetbrains.kotlin:kotlin-scripting-jvm:$kotlinVersion", repository)
        loadDependencies("org.jetbrains.kotlin:kotlin-scripting-jvm-host:$kotlinVersion", repository)
        loadDependencies("org.jetbrains.kotlin:kotlin-scripting-compiler-embeddable:$kotlinVersion", repository)
        loadDependencies("org.jetbrains.kotlin:kotlin-scripting-compiler-impl-embeddable:$kotlinVersion", repository)
        loadDependencies("org.jetbrains.intellij.deps:trove4j:1.0.20181211", repository)
        loadDependencies("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2", repository)
    }

    fun loadDependencies(source: String, repository: String) {
        val args = source.split(":")
        val downloader = DependencyDownloader(baseDir)
        if (properties.contains("repository-$repository")) {
            downloader.addRepository(Repository(properties.getProperty("repository-$repository")))
        } else {
            downloader.addRepository(Repository(repository))
        }
        val pomFile = File(baseDir, String.format("%s/%s/%s/%s-%s.pom", args[0].replace('.', '/'), args[1], args[2], args[1], args[2]))
        val pomShaFile = File(pomFile.path + ".sha1")
        if (pomFile.exists() && pomShaFile.exists() && PrimitiveIO.readFile(pomShaFile).startsWith(PrimitiveIO.getHash(pomFile))) {
            downloader.loadDependencyFromInputStream(pomFile.toPath().toUri().toURL().openStream())
        } else {
            val pom = String.format("%s/%s/%s/%s/%s-%s.pom", repository, args[0].replace('.', '/'), args[1], args[2], args[1], args[2])
            try {
                PrimitiveIO.println(String.format("Downloading library %s:%s:%s", args[0], args[1], args[2]))
                downloader.loadDependencyFromInputStream(URL(pom).openStream())
            } catch (ex: FileNotFoundException) {
                throw ex
            }
        }
        downloader.loadDependency(downloader.repositories, Dependency(args[0], args[1], args[2], DependencyScope.RUNTIME))
    }

    fun getFiles(file: File): List<File> {
        return when {
            file.isDirectory -> file.listFiles()?.flatMap { getFiles(it) } ?: emptyList()
            file.extension == "jar" -> listOf(file)
            else -> emptyList()
        }
    }

    fun getTabooModules(): List<File> {
        val files = mutableListOf<File>()
        val modules = PrimitiveSettings.INSTALL_MODULES.map {
            File(
                PrimitiveSettings.FILE_LIBS,
                String.format(
                    "%s/%s/%s/%s-%s.jar",
//                    PrimitiveLoader.TABOOLIB_GROUP.replace(".", "/"),
                    "io/izzel/taboolib",
                    it,
                    PrimitiveSettings.TABOOLIB_VERSION,
                    it,
                    PrimitiveSettings.TABOOLIB_VERSION
                )
            )
        }
        files += modules
        files += File("cache/taboolib/ink.ptms.artifex").listFiles() ?: arrayOf()
        return files
    }

    fun getKotlinFiles(): List<File> {
        val baseFile = File(PrimitiveSettings.FILE_LIBS)
        val files = ArrayList<File>()
        files += File(baseFile, "org/jetbrains/kotlin/kotlin-stdlib/$kotlinVersion/kotlin-stdlib-$kotlinVersion.jar")
        files += File(baseFile, "org/jetbrains/kotlin/kotlin-stdlib-common/$kotlinVersion/kotlin-stdlib-common-$kotlinVersion.jar")
//        files += File(baseFile, "org/jetbrains/kotlin/kotlin-stdlib-jdk7/$kotlinVersion/kotlin-stdlib-jdk7-$kotlinVersion.jar")
//        files += File(baseFile, "org/jetbrains/kotlin/kotlin-stdlib-jdk8/$kotlinVersion/kotlin-stdlib-jdk8-$kotlinVersion.jar")
        return files.filter { it.exists() }
    }
}