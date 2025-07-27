package ink.ptms.artifex.script.impl

import ink.ptms.artifex.Artifex
import ink.ptms.artifex.script.*
import ink.ptms.artifex.script.event.ScriptProjectReleasedEvent
import ink.ptms.artifex.script.event.ScriptProjectReloadedEvent
import ink.ptms.artifex.script.event.ScriptProjectStartedEvent
import taboolib.common.PrimitiveLoader
import taboolib.common.PrimitiveSettings
import taboolib.common.env.DependencyScope
import taboolib.common.env.legacy.Dependency
import taboolib.common.env.legacy.DependencyDownloader
import taboolib.common.env.legacy.Repository
import taboolib.common.io.newFile
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.console
import taboolib.common.platform.function.getDataFolder
import taboolib.common.util.ResettableLazy
import taboolib.common.util.resettableLazy
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Configuration
import taboolib.module.lang.sendLang
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList

/**
 * Artifex
 * ink.ptms.artifex.internal.DefaultScriptProject
 *
 * @author 坏黑
 * @since 2022/5/23 13:28
 */
abstract class DefaultScriptProject(val identifier: ScriptProjectIdentifier, val constructor: ScriptProjectConstructor) : ScriptProject {

    private val exchangeData = ConcurrentHashMap<String, Any>()
    private val runningScripts = ArrayList<Script>()

    val runningId = UUID.randomUUID().toString()

    val main: List<String>
        get() = identifier.root().getStringList("main")

    val repositories: List<String>
        get() = identifier.root().getStringList("repositories")

    val dependencies: List<String>
        get() = identifier.root().getStringList("dependencies")

    val autoMount: Boolean
        get() = identifier.root().getBoolean("auto-mount")

    // 根据配置文件中的依赖添加
    val runtimeProperty by resettableLazy("runtime-${name()}") {
        ScriptRuntimeProperty(mapOf("@Id" to runningId), mapOf()).apply {
            val downloader = DependencyDownloader(PrimitiveLoader.getLibraryFile())
            val repos = repositories.map { Repository(it) }.toMutableSet()
            repos.forEach { repo -> downloader.addRepository(repo) }
            repos.add(Repository())
            downloader.addRepository(Repository())
            val files = dependencies.flatMap {
                runCatching {
                    if (it.matches("^[^:\\s]+:[^:\\s]+:[^:\\s]+$".toRegex())) {
                        val args = it.split(":")
                        val groupId = args[0]
                        val artifactId = args[1]
                        val version = args[2]
                        val dependency = Dependency(groupId, artifactId, version, DependencyScope.RUNTIME)
                        console().sendLang(
                            "command-script-load-dependency",
                            identifier.name(),
                            dependency.groupId,
                            dependency.artifactId,
                            dependency.version
                        )
                        downloader.loadDependency(repos, dependency).map { it.findFile(PrimitiveLoader.getLibraryFile(), "jar") }
                    } else {
                        val file = File(it)
                        console().sendLang(
                            "command-script-load-file",
                            identifier.name(),
                            file.path
                        )
                        if (!file.exists()) {
                            return@flatMap emptyList()
                        }
                        if (file.isDirectory) {
                            return@flatMap file.listFiles()?.toList() ?: emptyList()
                        }
                        listOf(file)
                    }
                }.getOrElse {
                    it.printStackTrace()
                    emptyList()
                }
            }
            defaultClasspath.addAll(files)
        }
    }

    /**
     * 检查脚本是否可以启动
     *
     * @param sender 汇报接收者
     */
    abstract fun checkScripts(sender: ProxyCommandSender): Boolean

    /**
     * 整理脚本
     *
     * @param sender 汇报接收者
     * @param forceCompile 是否强制编译
     */
    abstract fun collectScripts(sender: ProxyCommandSender, forceCompile: Boolean = false): List<ScriptMeta>

    override fun runningId(): String {
        return runningId
    }

    override fun runningScripts(): List<Script> {
        return runningScripts
    }

    override fun disabled(): Boolean {
        return identifier.root().getBoolean("disable")
    }

    override fun root(): Configuration {
        return identifier.root()
    }

    override fun name(): String {
        return identifier.name()
    }

    override fun constructor(): ScriptProjectConstructor {
        return constructor
    }

    override fun run(sender: ProxyCommandSender, forceCompile: Boolean, logging: Boolean): Boolean {
        if (checkScripts(sender)) {
            // TODO ScriptProjectStartEvent
            if (logging) {
                sender.sendLang("project-start", name())
            }
            val scripts = collectScripts(sender, forceCompile)
            if (scripts.isEmpty()) {
                return false
            }
            if (Artifex.api().getScriptProjectManager().getRunningProject(name()) == null) {
                Artifex.api().getScriptProjectManager().applyProject(this)
            }
//            val property = runtimeProperty
//            scripts.forEach { runScript(it, sender, property) }
            scripts.forEach { runScript(it, sender) }
            if (logging) {
                sender.sendLang("command-project-started", name())
            }
            val event = ScriptProjectStartedEvent(
                this,
                sender,
                scripts,
                logging,
                forceCompile
            )
            Artifex.api().getScriptEventBus().call(event)
            return true
        }
        return false
    }

    override fun reload(sender: ProxyCommandSender, forceCompile: Boolean, logging: Boolean): Boolean {
        // TODO ScriptProjectReloadEvent
        if (logging) {
            sender.sendLang("project-reload", name())
        }
        // 先重载 identifier 确保依赖能够被正确使用
        reloadConfig()
        ResettableLazy.reset("runtime-${name()}")
        val scripts = collectScripts(sender, forceCompile)
        if (scripts.isEmpty()) {
            return false // 若未成功编译则不会继续执行
        }
        releaseAll(sender, false)
//            val property = runtimeProperty
//            scripts.forEach { runScript(it, sender, property) }
        scripts.forEach { runScript(it, sender) }
        if (logging) {
            sender.sendLang("command-project-reloaded", name())
        }
        val event = ScriptProjectReloadedEvent(
            this,
            sender,
            scripts,
            logging,
            forceCompile
        )
        Artifex.api().getScriptEventBus().call(event)
        return true
    }

    override fun release(sender: ProxyCommandSender, logging: Boolean) {
        // TODO ScriptProjectReleaseEvent
        if (logging) {
            sender.sendLang("project-release", name())
        }
        releaseAll(sender, logging)
        Artifex.api().getScriptProjectManager().releaseProject(name())
        if (logging) {
            sender.sendLang("command-project-released", name())
        }
        val event = ScriptProjectReleasedEvent(
            this,
            sender,
            logging
        )
        Artifex.api().getScriptEventBus().call(event)
    }

    override fun isRunning(): Boolean {
        return runningScripts.isNotEmpty()
    }

    override fun reloadConfig() {
        identifier.root().reload()
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> exchangeData(name: String): T? {
        return exchangeData[name] as? T
    }

    override fun exchangeData(name: String, value: Any) {
        exchangeData[name] = value
    }

    override fun exchangeData(): MutableMap<String, Any> {
        return exchangeData
    }

    /**
     * 运行脚本
     */
    open fun runScript(scriptMeta: ScriptMeta, sender: ProxyCommandSender) {
        val data = Artifex.api().getScriptContainerManager().getExchangeData(runningId)
        // 项目数据
        data["@Project"] = this
        // 先加载依赖

        val runtimeProperty = this.runtimeProperty
        // 运行脚本
        Artifex.api().getScriptHelper().getSimpleEvaluator().prepareEvaluation(scriptMeta, sender, loggingRunning = false)
            .loggingMounted(false)
            .mount(autoMount)
            .afterEval {
                runningScripts += it
                it.container().exchangeData()["@Project"] = this@DefaultScriptProject
            }
            .apply(runtimeProperty)
    }

    /**
     * 释放所有资源
     */
    open fun releaseAll(sender: ProxyCommandSender, logging: Boolean) {
        // 释放脚本
        runningScripts.forEach { releaseScript(sender, it.container(), logging) }
        runningScripts.clear()
        // 注销交换数据
        exchangeData.clear()
        Artifex.api().getScriptContainerManager().resetExchangeData(runningId)
    }

    /**
     * 释放脚本
     */
    open fun releaseScript(sender: ProxyCommandSender, container: ScriptContainer, logging: Boolean) {
        if (logging) {
            Artifex.api().getScriptHelper().releaseScript(container, sender, releaseImplementations = true)
        } else {
            container.releaseSafely(true)
        }
    }
}