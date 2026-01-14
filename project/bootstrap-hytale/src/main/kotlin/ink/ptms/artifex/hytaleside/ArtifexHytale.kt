package ink.ptms.artifex.hytaleside

import com.hypixel.hytale.common.plugin.PluginIdentifier
import com.hypixel.hytale.server.core.plugin.PluginManager
import ink.ptms.artifex.PlatformHelper
import taboolib.common.LifeCycle
import taboolib.common.platform.*
import taboolib.common.platform.function.releaseResourceFile
import taboolib.common.util.unsafeLazy
import taboolib.platform.VelocityPlugin
import kotlin.jvm.optionals.getOrNull

/**
 * Artifex
 * ink.ptms.artifex.hytaleside.ArtifexHytale
 *
 * @author scorez
 * @since 1/14/26 00:00.
 */
@PlatformSide(Platform.HYTALE)
object ArtifexHytale : Plugin(), PlatformHelper {

    @Awake(LifeCycle.INIT)
    fun init() {
        releaseResourceFile("runtime/hytale-api.jar", false)
    }

    override fun onLoad() {
        PlatformFactory.awokenMap["ink.ptms.artifex.PlatformHelper"] = this
    }

    override fun plugin(fullName: String): Any? {
        val identifier = if (fullName.contains(":")) {
            val (namespace, name) = fullName.split(":")
            PluginIdentifier(namespace, name)
        } else PluginIdentifier("taboolib", fullName)
        return PluginManager.get().getPlugin(identifier)
    }

    override fun plugins(): List<Any> {
        return PluginManager.get().plugins
    }
}