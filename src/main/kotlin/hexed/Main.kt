package hexed

import arc.util.CommandHandler
import arc.util.Log
import hexed.coreplugin.HexMapManager
import hexed.generation.Generators
import hexed.managers.Game
import mindurka.api.Consts
import mindurka.api.Gamemode
import mindurka.coreplugin.CorePlugin
import mindustry.Vars
import mindustry.core.NetServer.TeamAssigner
import mindustry.game.Team
import mindustry.mod.Plugin

class Main : Plugin() {
    override fun init() {
        CorePlugin.init(javaClass.classLoader)
        Gamemode.maps = HexMapManager()

        // Assigned players to derelict team by default
        Vars.netServer.assigner = TeamAssigner { _, _ -> Team.derelict }

        // Override coreplugin game over
        // Only for hexed
        Consts.serverControl.gameOverListener = {}
    }

    override fun registerServerCommands(handler: CommandHandler) {
        handler.register("host", "[generator...]", "Open the server in hexed mode.") { args ->
            if (!Vars.state.isMenu) {
                Log.err("Already hosting. Type 'stop' to stop hosting first.")
                return@register
            }

            val generator = if (args.isNotEmpty()) {
                Generators.findByName(args[0]) ?: run {
                    Log.err("Map with this name was not found.")
                    return@register
                }
            } else {
                Generators.random(null)
            }

            Game.generate(generator)
            Vars.netServer.openServer()
        }
    }
}