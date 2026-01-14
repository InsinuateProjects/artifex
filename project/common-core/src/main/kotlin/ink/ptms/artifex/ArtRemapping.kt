package ink.ptms.artifex

import ink.ptms.artifex.kotlin.compilerOutputFiles
import taboolib.common.platform.function.warning
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.commons.ClassRemapper
import org.objectweb.asm.commons.Remapper
import kotlin.script.experimental.api.CompiledScript
import kotlin.script.experimental.jvm.impl.KJvmCompiledScript

fun CompiledScript.remap(key: String = "default"): CompiledScript {
    if (this is KJvmCompiledScript) {
        runCatching {
            // K2 兼容：使用 KotlinUtils 中的扩展函数，避免直接依赖内部类
            val compilerOutputFiles = this.compilerOutputFiles()
            val mapValues = compilerOutputFiles.mapValues {
                if (it.key.endsWith(".class")) {
                    val classReader = ClassReader(it.value)
                    val classWriter = ClassWriter(ClassWriter.COMPUTE_MAXS)
                    val classVisitor: ClassVisitor = ClassRemapper(classWriter, Artifex.api().getScriptCompiler().getRemapper(key))
                    classReader.accept(classVisitor, 0)
                    classWriter.toByteArray()
                } else {
                    it.value
                }
            }
            (compilerOutputFiles as MutableMap).putAll(mapValues)
        }.onFailure {
            it.printStackTrace()
        }
    } else {
        warning("Unsupported script type: ${this::class.simpleName}")
    }
    return this
}