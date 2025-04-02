package ink.ptms.artifex.script.event

import ink.ptms.artifex.script.Script
import ink.ptms.artifex.script.ScriptMeta
import ink.ptms.artifex.script.ScriptProject
import taboolib.common.platform.ProxyCommandSender

/**
 * Artifex
 * ink.ptms.artifex.script.event.ScriptProjectStartEvent
 *
 * @author scorez
 * @since 1/18/25 23:15.
 */
class ScriptProjectStartedEvent(
    val project: ScriptProject,
    val sender: ProxyCommandSender,
    val scripts: List<ScriptMeta>,
    val logging: Boolean,
    val forceCompile: Boolean,
) : ScriptEvent {
}