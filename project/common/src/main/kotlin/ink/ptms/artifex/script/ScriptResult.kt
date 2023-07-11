package ink.ptms.artifex.script

/**
 * 脚本运行结果
 *
 * @author 坏黑
 * @since 2022/5/15 23:47
 */
interface ScriptResult<out R> {

    /**
     * 运行结果
     */
    fun value(): R?

    /**
     * 运行报告
     */
    fun reports(): List<Diagnostic>

    /**
     * 是否运行成功
     */
    fun isSuccessful(): Boolean

    /**
     * 运行报告详细信息
     */
    data class Diagnostic(val code: Int, val message: String, val severity: Severity, val source: Source, val exception: Throwable?) {

        /**
         * 是否可以被忽略
         */
        fun isIgnored(): Boolean {
            return severity == Severity.DEBUG || message.contains("never used") || message.contains("deprecated")
        }

        override fun toString(): String {
            val loc = source.location
            val pos = if (loc != null) "(Line: ${loc.start.line}, Column: ${loc.start.col}): " else ""
            return if (source.path != null) {
                val path = source.path.substringAfterLast('/')
                "${severity}: ${pos}${message} ($path)"
            } else {
                "${severity}: ${pos}${message}"
            }
        }
    }

    /**
     * 脚本源
     */
    data class Source(val path: String?, val location: ScriptSourceCode.Location?)

    /**
     * 脚本运行结果返回值
     */
    sealed class Result(val scriptClass: Class<*>?, val instance: Any?) {

        /**
         * 正常结果
         */
        class Value(val name: String, val value: Any?, val type: String, scriptClass: Class<*>?, instance: Any?) : Result(scriptClass, instance) {

            override fun toString(): String = "$name: $type = $value"
        }

        /**
         * 无返回值
         */
        class Unit(scriptClass: Class<*>, instance: Any?) : Result(scriptClass, instance) {

            override fun toString(): String = "Unit"
        }

        /**
         * 错误结果
         */
        class Error(val error: Throwable, val wrappingException: Throwable? = null, scriptClass: Class<*>? = null) : Result(scriptClass, null) {

            override fun toString(): String {
                error.printStackTrace()
                return error.toString()
            }
        }

        /**
         * 未执行成功
         */
        object NotEvaluated : Result(null, null)
    }

    /**
     * 报告类型
     */
    enum class Severity(val color: String) {

        DEBUG("§8"), INFO("§7"), WARNING("§6"), ERROR("§c"), FATAL("§4")
    }
}