package ink.ptms.artifex.appside

import ink.ptms.artifex.PlatformHelper
import taboolib.common.LifeCycle
import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.*
import taboolib.common.platform.function.releaseResourceFile
import taboolib.common.util.unsafeLazy
import taboolib.platform.VelocityPlugin
import kotlin.jvm.optionals.getOrNull

/**
 * Artifex
 * ink.ptms.artifex.velocityside.ArtifexVelocity
 *
 * @author scorez
 * @since 4/21/24 13:43.
 */
@RuntimeDependencies(
    RuntimeDependency(
        "!org.jetbrains.kotlin:kotlin-reflect:1.8.22",
        repository = "https://maven.aliyun.com/repository/central",
    ),
    RuntimeDependency(
        "!org.jetbrains.kotlin:kotlin-stdlib:1.8.22",
        repository = "https://maven.aliyun.com/repository/central",
    ),
)
@PlatformSide(Platform.APPLICATION)
object ArtifexApplication : Plugin(), PlatformHelper {

    override fun onLoad() {
        PlatformFactory.awokenMap["ink.ptms.artifex.PlatformHelper"] = this
    }

    override fun plugin(name: String): Any? {
        return null
    }

    override fun plugins(): List<Any> {
        return listOf()
    }
}