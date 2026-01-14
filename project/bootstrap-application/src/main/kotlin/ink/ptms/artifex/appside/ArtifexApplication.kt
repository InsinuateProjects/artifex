package ink.ptms.artifex.appside

import ink.ptms.artifex.Artifex
import ink.ptms.artifex.PlatformHelper
import ink.ptms.artifex.script.ScriptMeta
import ink.ptms.artifex.script.ScriptProjectIdentifier
import taboolib.common.LifeCycle
import taboolib.common.TabooLib
import taboolib.common.io.runningClassMapInJar
import taboolib.common.platform.*
import taboolib.common.platform.function.console
import taboolib.common.platform.function.info
import taboolib.common.platform.function.warning
import taboolib.common.platform.service.PlatformIO
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.Reflex.Companion.invokeMethod
import taboolib.library.reflex.Reflex.Companion.setProperty
import taboolib.library.reflex.ReflexClass
import taboolib.platform.AppConsole
import taboolib.platform.AppIO
import java.util.concurrent.ConcurrentHashMap
import kotlin.system.exitProcess

/**
 * Artifex
 * ink.ptms.artifex.velocityside.ArtifexVelocity
 *
 * @author scorez
 * @since 4/21/24 13:43.
 */
@PlatformSide(Platform.APPLICATION)
object ArtifexApplication : Plugin(), PlatformHelper {

    @Awake(LifeCycle.INIT)
    fun init() {
        val adapter = AppAdapter()
        val adapterKey = PlatformFactory.serviceMap.keys.first { it.contains("PlatformAdapter") }
        PlatformFactory.serviceMap[adapterKey] = adapter
    }

    @Awake(LifeCycle.LOAD)
    fun load() {
        PlatformFactory.awokenMap["ink.ptms.artifex.PlatformHelper"] = this
        // 仅构建时不自启动
        if (Main.requestProjects.isNotEmpty()) {
            (runningClassMapInJar as ConcurrentHashMap<String, ReflexClass>).remove("ink.ptms.artifex.script.impl.DefaultScriptProjectManager")
            // 需要额外触发 init
            Class.forName("ink.ptms.artifex.script.impl.DefaultScriptProjectManager")
                .getProperty<Any>("INSTANCE", isStatic = true)!!
                .invokeMethod<Void>("unload")
        }
    }

    @Awake(LifeCycle.ENABLE)
    fun enable() {
        if (Main.requestProjects.isNotEmpty()) {
            Main.requestProjects.forEach {
                val identifier = Artifex.api().getScriptProjectManager().getProject(it) as? ScriptProjectIdentifier.DevIdentifier
                    ?: return@forEach info("Project $it does not exist.")
                val project = identifier.load()
                info("Processing project $it.")
                val metas = project.invokeMethod<List<ScriptMeta>>("collectScripts", console(), false)!!
                if (metas.isEmpty()) {
                    warning("Failed to build project $it.")
                } else {
                    info("Project $it was evaluated in ${identifier.file.name}.")
                }
            }
            exitProcess(0)
        }
    }

    override fun plugin(name: String): Any? {
        return null
    }

    override fun plugins(): List<Any> {
        return listOf()
    }
}