package ink.ptms.artifex.appside

import ink.ptms.artifex.PlatformHelper
import taboolib.common.LifeCycle
import taboolib.common.platform.*
import taboolib.common.platform.service.PlatformIO
import taboolib.library.reflex.Reflex.Companion.setProperty
import taboolib.platform.AppConsole
import taboolib.platform.AppIO

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
    }

    @Awake(LifeCycle.ENABLE)
    fun enable() {
    }

    override fun plugin(name: String): Any? {
        return null
    }

    override fun plugins(): List<Any> {
        return listOf()
    }
}