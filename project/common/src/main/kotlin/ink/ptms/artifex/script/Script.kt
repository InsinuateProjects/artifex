package ink.ptms.artifex.script

/**
 * 脚本对象
 *
 * @author 坏黑
 * @since 2022/5/15 23:12
 */
abstract class Script : Exchanges {

    /**
     * 脚本序号
     */
    abstract fun baseId(): String

    /**
     * 脚本对象
     */
    abstract fun baseScript(): ScriptCompiled

    /**
     * 脚本容器
     */
    abstract fun container(): ScriptContainer

    /**
     * 释放脚本资源（卸载脚本）
     */
    open fun release() {}

    /**
     * 开放接口，用于从外部进行调用，
     * 这只是 Artifex 的规范，不是必须遵守的写法
     */
    open fun invoke(method: String, args: Array<out Any>): Any? {
        return null
    }
}