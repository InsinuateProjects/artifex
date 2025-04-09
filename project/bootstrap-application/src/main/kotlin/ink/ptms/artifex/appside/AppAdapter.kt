package ink.ptms.artifex.appside

import taboolib.common.Inject
import taboolib.common.platform.*
import taboolib.common.platform.service.PlatformAdapter
import taboolib.common.util.Location
import taboolib.platform.AppConsole

/**
 * Artifex
 * ink.ptms.artifex.appside.AppAdapter
 *
 * @author scorez
 * @since 4/5/25 17:32.
 */
@Awake
@Inject
@PlatformSide(Platform.APPLICATION)
class AppAdapter : PlatformAdapter {

    override fun console(): ProxyCommandSender {
        return AppConsole
    }

    override fun onlinePlayers(): List<ProxyPlayer> {
        return listOf()
    }

    override fun adaptPlayer(any: Any): ProxyPlayer {
        TODO("Not yet implemented")
    }

    override fun adaptCommandSender(any: Any): ProxyCommandSender {
        return AppConsole
    }

    override fun adaptLocation(any: Any): Location {
        TODO("Not yet implemented")
    }

    override fun platformLocation(location: Location): Any {
        TODO("Not yet implemented")
    }

    override fun allWorlds(): List<String> {
        TODO("Not yet implemented")
    }

}