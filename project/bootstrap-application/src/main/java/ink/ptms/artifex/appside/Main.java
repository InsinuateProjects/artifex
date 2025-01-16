package ink.ptms.artifex.appside;

import taboolib.platform.App;
import taboolib.platform.AppEnv;

/**
 * Artifex
 * ink.ptms.artifex.appside.Main
 *
 * @author scorez
 * @since 1/15/25 21:00.
 */
public class Main {
    public static void main(String[] args) {
        AppEnv env = App.env();
        // 兼容运行, 确保脚本能够通用
        env.skipKotlinRelocate(false);
        env.skipSelfRelocate(false);
        App.init();
    }
}
