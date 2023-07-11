package ink.ptms.artifex.script

import java.io.File

/**
 * 编译成功的脚本
 *
 * @author 坏黑
 * @since 2022/5/15 23:20
 */
interface ScriptCompiled {

    /**
     * 获取脚本名称
     */
    fun name(): String

    /**
     * 获取嵌入的其他脚本名称
     */
    fun otherIncludeScripts(): List<String>

    /**
     * 获取引用的其他脚本名称
     */
    fun otherImportScripts(): List<String>

    /**
     * 运行脚本（不会触发 [ScriptEvaluateEvent] 事件）
     *
     * @param id 脚本序号
     * @param props 脚本运行参数
     */
    fun invoke(id: String, props: ScriptRuntimeProperty): ScriptResult<ScriptResult.Result>

    /**
     * 运行脚本（不会触发 [ScriptEvaluateEvent] 事件）
     *
     * @param configuration 脚本运行配置
     */
    fun invoke(configuration: ScriptEvaluator.Configuration): ScriptResult<ScriptResult.Result>

    /**
     * 生成 Jar 并写入文件
     */
    fun generateScriptJar(file: File)

    /**
     * 生成 ScriptMeta 对象
     */
    fun generateScriptMeta(): ScriptMeta
}