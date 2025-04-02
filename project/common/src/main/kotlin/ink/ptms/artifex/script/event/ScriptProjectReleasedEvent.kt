package ink.ptms.artifex.script.event

import ink.ptms.artifex.script.Script
import ink.ptms.artifex.script.ScriptMeta
import ink.ptms.artifex.script.ScriptProject
import taboolib.common.platform.ProxyCommandSender

/**
 * Artifex
 * ink.ptms.artifex.script.event.ScriptProjectReleasedEvent
 *
 * @author scorez
 * @since 1/18/25 23:15.
 */
class ScriptProjectReleasedEvent(
    val project: ScriptProject,
    val sender: ProxyCommandSender,
    val logging: Boolean
) : ScriptEvent {
}