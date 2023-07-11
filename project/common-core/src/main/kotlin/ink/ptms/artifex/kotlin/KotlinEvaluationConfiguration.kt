package ink.ptms.artifex.kotlin

import ink.ptms.artifex.script.ScriptCompiled
import ink.ptms.artifex.script.ScriptEvaluator
import ink.ptms.artifex.script.ScriptRuntimeProperty
import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.api.constructorArgs
import kotlin.script.experimental.api.providedProperties
import kotlin.script.experimental.api.scriptsInstancesSharing
import kotlin.script.experimental.jvm.baseClassLoader
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvm.loadDependencies

/**
 * 运行配置
 */
class KotlinEvaluationConfiguration(val id: String, val props: ScriptRuntimeProperty, val script: ScriptCompiled) : ScriptEvaluationConfiguration(
    {
        constructorArgs(id, script)
        scriptsInstancesSharing(true)
        jvm {
            baseClassLoader(KotlinScript::class.java.classLoader)
            loadDependencies(false)
        }
        val map = props.providedProperties.map { it.key.toString() to it.value }.toMutableList<Pair<String, *>>()
        map += "runArgs" to props.runArgs
        providedProperties(*map.toTypedArray())
    }
), ScriptEvaluator.Configuration