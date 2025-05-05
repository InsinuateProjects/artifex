package ink.ptms.artifex.scripting

import ink.ptms.artifex.Artifex
import ink.ptms.artifex.script.Script
import ink.ptms.artifex.script.ScriptProject

/**
 * Artifex
 * ink.ptms.artifex.scripting.Scripting
 *
 * @author scorez
 * @since 4/23/25 02:16.
 */

/**
 * 获取一个项目
 */
fun Script.getProject(name: String): ScriptProject? {
    return Artifex.api().getScriptProjectManager().getRunningProject(name)
}

/**
 * 获取所有项目
 */
fun Script.getProjects(): List<ScriptProject> {
    return Artifex.api().getScriptProjectManager().getRunningProjects()
}

/**
 * 获取所有运行中的脚本实例
 */
fun Script.getScripts(): List<Script> {
    return Artifex.api().getScriptContainerManager().getAll().map { it.script() }
}

/**
 * 获取一个脚本实例, baseId 首字符大写
 */
fun Script.getScript(baseId: String): Script? {
    return Artifex.api().getScriptContainerManager().get(baseId)?.script()
}