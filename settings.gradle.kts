rootProject.name = "artifex"

include("plugin")
include("project:common")
// 实现
include("project:common-impl-default")
include("project:common-impl-project")
// 脚本运行环境（核心）
include("project:common-core")
// 脚本标准库
include("project:common-script-api")
include("project:common-script-api-bukkit")
include("project:common-script-api-bungee")
include("project:common-script-api-velocity")
//include("project:common-script-api-application")
// 运行平台
include("project:bootstrap-bukkit")
include("project:bootstrap-bungee")
include("project:bootstrap-velocity")
include("project:bootstrap-application")
// jar 代理
include("project:jar-proxy-bukkit")
include("project:jar-proxy-bungee")
include("project:jar-proxy-velocity")
//include("project:jar-proxy-application")

// 经过处理的 reflex
include("project:common-core-reflex")