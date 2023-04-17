package ink.ptms.artifex.internal

import ink.ptms.artifex.Artifex
import ink.ptms.artifex.ArtifexAPI
import ink.ptms.artifex.PlatformHelper
import ink.ptms.artifex.script.*
import taboolib.common.LifeCycle
import taboolib.common.io.getInstance
import taboolib.common.platform.Awake
import taboolib.common.platform.PlatformFactory
import taboolib.common.platform.SkipTo
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.releaseResourceFile
import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigNode
import taboolib.module.configuration.Configuration
import java.io.File

/**
 * Artifex
 * ink.ptms.artifex.internal.ArtScriptAPI
 *
 * @author 坏黑
 * @since 2022/5/16 00:41
 */
@SkipTo(LifeCycle.INIT)
object DefaultScriptAPI : ArtifexAPI {

    @Config
    lateinit var conf: Configuration
        private set

    @ConfigNode("ignore-warning")
    lateinit var ignoreWarning: List<String>
        private set

    @ConfigNode("scriptFolder")
    var scriptFolder: String? = null
        private set

    private var isDependenciesLoaded = false

    private val helper = DefaultScriptHelper()
    private val environment = DefaultScriptEnvironment()
    private val classLoader = DefaultRuntimeClassLoader(getRuntimeLibraryFile())
    private val containerManager = DefaultScriptContainerManager()

    private val compiler = loadRuntimeClass<ScriptCompiler>("ArtScriptCompiler")
    private val evaluator = loadRuntimeClass<ScriptEvaluator>("ArtScriptEvaluator")
    private val metaHandler = loadRuntimeClass<ScriptMetaHandler>("ArtScriptMetaHandler")

    init {
        Artifex.register(DefaultScriptAPI)
    }

    @Awake(LifeCycle.LOAD)
    fun init() {
        Artifex.api().getScriptEnvironment().setupGlobalImports()
    }

    @Awake(LifeCycle.DISABLE)
    fun cancel() {
        classLoader.close()
    }

    override fun getPlatformHelper(): PlatformHelper {
        return PlatformFactory.getAPI()
    }

    override fun getScriptHelper(): ScriptHelper {
        return helper
    }

    override fun getScriptCompiler(): ScriptCompiler {
        return compiler
    }

    override fun getScriptEvaluator(): ScriptEvaluator {
        return evaluator
    }

    override fun getScriptEnvironment(): ScriptEnvironment {
        return environment
    }

    override fun getScriptMetaHandler(): ScriptMetaHandler {
        return metaHandler
    }

    override fun getScriptClassLoader(): RuntimeClassLoader {
        return classLoader
    }

    override fun getScriptProjectManager(): ScriptProjectManager {
        return PlatformFactory.getAPI()
    }

    override fun getScriptContainerManager(): ScriptContainerManager {
        return containerManager
    }

    override fun getRuntimeLibraryFile(): List<File> {
        if (!isDependenciesLoaded) {
            isDependenciesLoaded = true
            KotlinEnvironments.loadDependencies()
        }
        val file = File(getDataFolder(), "runtime/core.jar")
        kotlin.runCatching {
            releaseResourceFile("runtime/core.jar", true)
            releaseResourceFile("runtime/bridge.jar", true)
        }
        if (file.nonExists()) {
            error("Runtime library not found")
        }
        return environment.classpathWithoutPlugins(listOf(DefaultScriptAPI::class.java))
    }

    override fun getStatus(): Map<String, String> {
        val map = HashMap<String, String>()
        kotlin.runCatching {
            // 可能缺失
            map["PlatformHelper"] = kotlin.runCatching { getPlatformHelper().javaClass.name }.getOrElse { "null" }
            map["ScriptHelper"] = getScriptHelper().javaClass.name
            map["ScriptCompiler"] = getScriptCompiler().javaClass.name
            map["ScriptEvaluator"] = getScriptEvaluator().javaClass.name
            map["ScriptEnvironment"] = getScriptEnvironment().javaClass.name
            map["ScriptMetaHandler"] = getScriptMetaHandler().javaClass.name
            map["ScriptClassLoader"] = getScriptClassLoader().javaClass.name
            // 可能缺失
            map["ScriptProjectManager"] = kotlin.runCatching { getScriptProjectManager().javaClass.name }.getOrElse { "null" }
            map["ScriptContainerManager"] = getScriptContainerManager().javaClass.name
        }
        return map
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> loadRuntimeClass(name: String): T {
        val instance = classLoader.findClass("ink.ptms.artifex.$name").getInstance(true) ?: error("Class not found: $name")
        return instance.get() as T
    }
}