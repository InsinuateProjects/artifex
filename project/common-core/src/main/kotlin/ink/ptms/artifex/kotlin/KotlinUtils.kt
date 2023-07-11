package ink.ptms.artifex.kotlin

import ink.ptms.artifex.Artifex
import ink.ptms.artifex.ImportScript
import ink.ptms.artifex.script.ScriptResult
import ink.ptms.artifex.script.ScriptSourceCode
import org.jetbrains.kotlin.scripting.compiler.plugin.impl.KJvmCompiledModuleInMemoryImpl
import java.io.File
import kotlin.script.experimental.api.CompiledScript
import kotlin.script.experimental.api.ScriptCompilationConfigurationKeys
import kotlin.script.experimental.api.ScriptDiagnostic
import kotlin.script.experimental.api.SourceCode
import kotlin.script.experimental.jvm.impl.KJvmCompiledScript
import kotlin.script.experimental.util.PropertiesCollection

val ScriptCompilationConfigurationKeys.artifexProperties by PropertiesCollection.key<Map<String, Any>>()

val scriptsFile: File
    get() = Artifex.api().getScriptHelper().baseScriptFolder()

fun File.isKts(include: String): Boolean {
    return (name == include && extension == "kts") || name == "$include.kts"
}

fun File.isJar(include: String): Boolean {
    return (name == include && extension == "jar") || name == "$include.jar"
}

fun File.searchFile(match: File.() -> Boolean): Set<File> {
    return when {
        isDirectory -> listFiles()?.flatMap { it.searchFile(match) }?.toSet() ?: emptySet()
        match(this) -> setOf(this)
        else -> emptySet()
    }
}

fun positionFromKt(position: SourceCode.Position): ScriptSourceCode.Position {
    return ScriptSourceCode.Position(position.line + 1, position.col, position.absolutePos)
}

fun positionFromArt(position: ScriptSourceCode.Position): SourceCode.Position {
    return SourceCode.Position(position.line, position.col, position.absolutePos)
}

fun diagnosticFromKt(diagnostic: ScriptDiagnostic): ScriptResult.Diagnostic {
    val loc = diagnostic.location
    val location = if (loc != null) {
        ScriptSourceCode.Location(positionFromKt(loc.start), if (loc.end != null) positionFromKt(loc.end!!) else null)
    } else {
        null
    }
    return ScriptResult.Diagnostic(
        diagnostic.code,
        diagnostic.message,
        ScriptResult.Severity.valueOf(diagnostic.severity.name),
        ScriptResult.Source(diagnostic.sourcePath, location),
        diagnostic.exception
    )
}

fun diagnosticFromKt(diagnostic: ScriptResult.Diagnostic): ScriptDiagnostic {
    val start = diagnostic.source.location?.start?.let { positionFromArt(it) }
    val end = diagnostic.source.location?.end?.let { positionFromArt(it) }
    return ScriptDiagnostic(
        diagnostic.code,
        diagnostic.message,
        ScriptDiagnostic.Severity.valueOf(diagnostic.severity.name),
        diagnostic.source.path,
        if (start != null) SourceCode.Location(start, end) else null,
        diagnostic.exception
    )
}

fun CompiledScript.scriptClassFQName(): String {
    return when (this) {
        is KJvmCompiledScript -> scriptClassFQName
        is ImportScript -> scriptClassFQName
        else -> error("Unsupported $this")
    }
}

fun CompiledScript.compilerOutputFiles(): Map<String, ByteArray> {
    return when (this) {
        is KJvmCompiledScript -> (getCompiledModule() as? KJvmCompiledModuleInMemoryImpl)?.compilerOutputFiles ?: HashMap()
        is ImportScript -> compilerOutputFiles
        else -> error("Unsupported $this")
    }
}

fun checkImportScript(
    file: File?,
    script: CompiledScript,
    compilerOutputFiles: MutableMap<String, ByteArray>,
    imports: List<Pair<File, String>>,
): CompiledScript {
    val otherScripts = script.otherScripts.map { checkImportScript(null, it, compilerOutputFiles, imports) }
    return if (imports.any { i -> i.second == script.scriptClassFQName() }) {
        ImportScript(file, script.scriptClassFQName(), compilerOutputFiles, otherScripts)
    } else {
        script
    }
}