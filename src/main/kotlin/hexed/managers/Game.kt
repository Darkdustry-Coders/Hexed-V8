package hexed.managers

import arc.Events
import arc.func.Cons
import arc.struct.Seq
import arc.util.Interval
import arc.util.Log
import arc.util.Time
import arc.util.Timer
import hexed.Config
import hexed.Renderer
import hexed.generation.Generator
import hexed.generation.Generators
import mindustry.Vars
import mindustry.game.EventType
import mindustry.game.EventType.GameOverEvent
import mindustry.game.EventType.PlayEvent
import mindustry.game.EventType.ResetEvent
import mindustry.game.Team
import mindustry.gen.Call
import mindustry.gen.Groups
import mindustry.net.WorldReloader

object Game {
    lateinit var generator: Generator

    val interval: Interval = Interval(4)

    var restarting = false
    var counter = Config.ROUND_TIME

    // List of managers. Add new managers here
    val managers: Seq<Manager> = Seq.with(
        Hexes,
        Session,
        Requests
    )

    init {

        Events.run(EventType.Trigger.update) {
            update()
            Team.derelict.data().cores.each(Cons { core -> core.tile.removeNet() })
            Team.derelict.data().units.each(Cons { unit -> Call.unitDespawn(unit) })
        }

        // IDK why, but class.java is needed
        Events.run(ResetEvent::class.java) {
            reset()
        }

        Events.run(PlayEvent::class.java) {
            play()
        }

        // Not called when the round ends via RTV
        Events.run(GameOverEvent::class.java) {
            gameover()
        }
    }

    fun reset() {
        managers.each { it.reset() }
    }

    fun play() {
        managers.each { it.init() }
        generator.applyRules(Vars.state.rules) // RTV does not work without this (reason unknown)
        restarting = false
        counter = Config.ROUND_TIME
    }

    fun update() {
        if (restarting) return
        if (Groups.player.isEmpty) return

        Hexes.update()

        if (interval.get(0, Config.HUD_TIME))
            Renderer.showHudText()

        if (interval.get(1, Config.LEADERBOARD_TIME))
            Renderer.showLeaderboard()

        if (interval.get(2, Config.CHECK_WIN_TIME))
            checkWinState()

        if (counter <= 0) {
            Events.fire(GameOverEvent(Team.derelict))
        }

        counter -= Time.delta
    }

    fun checkWinState() {
        val candidates = Session.parties.values().toSeq()
        if (candidates.isEmpty) return

        // The game ends if a player controls all required hexes,
        // or if they have enough kills and are the only team left
        val winner = candidates.find { it.controlled >= Config.WIN_HEXES }
            ?: candidates.singleOrNull()?.takeIf { it.kills >= 1 } ?: return

        Events.fire(GameOverEvent(winner.team))
    }

    fun gameover() {
        if (restarting) return
        restarting = true

        Timer.schedule({ Renderer.showEndGameMessage() }, 2f)
        Timer.schedule({ generate(Generators.random(generator)) }, 12f)
    }

    fun generate(generator: Generator) {
        Game.generator = generator

        val reloader = WorldReloader()
        reloader.begin()

        Vars.logic.reset()

        Log.info("Generating location with generator @...", generator.name)
        Vars.world.loadGenerator(Config.WIDTH, Config.HEIGHT, generator::generate)
        Log.info("Location generated.")

        Vars.logic.play()

        reloader.end()
    }

    fun rtv(nextGenerator: Generator) {
        if (restarting) return
        restarting = true

        generator = nextGenerator
        // For RTV, just load the world without a full reload
        Vars.world.loadGenerator(Config.WIDTH, Config.HEIGHT, generator::generate)
    }
}


