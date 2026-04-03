package hexed.structures

import arc.Core
import arc.graphics.Color
import arc.math.Mathf
import arc.math.geom.Intersector
import arc.math.geom.Position
import arc.struct.IntSeq
import hexed.Config
import hexed.utils.HexUtils
import hexed.managers.Game
import hexed.managers.Session

import mindustry.Vars
import mindustry.content.Fx
import mindustry.game.Team
import mindustry.gen.Call
import mindustry.world.blocks.storage.CoreBlock
import mindustry.world.blocks.storage.CoreBlock.CoreBuild

import hexed.Config.RADIUS
import mindustry.entities.Damage
import java.util.Arrays

class Hex(val x: Int, val y: Int, val id: Int) : Position {
    val progress = IntSeq(IntArray(256))
    var owner: Party? = null
    var needsUpdate: Boolean = false

    val hasCore: Boolean
        get() = Vars.world.build(x, y) is CoreBuild

    fun requestUpdate() {
        needsUpdate = true
    }

    fun update() {
        needsUpdate = false
        updateProgress()

        val team = getTeam()

        owner = Session.parties[team]
        if (owner == null) return

        if (owner!!.controlled > 1) owner!!.active = true

        if (hasCore) return
        Vars.world.tile(x, y).setNet(Game.generator.planet.defaultCore, team, 0)

    }

    fun getTeam(): Team {
        if (hasCore) return Vars.world.tile(x, y).team()

        val data = Vars.state.teams.getActive().max { team -> getProgress(team!!.team).toFloat() }
        if (data == null) return Team.derelict

        if (getProgress(data.team) < Config.PROGRESS_REQUIREMENT) return Team.derelict

        return data.team
    }

    fun destroy(defender: Team, attacker: Team?) {
        if (attacker == Team.derelict) return

        //Damage.dynamicExplosion(x.toFloat(), y.toFloat(), 10f, 10f, 10f, RADIUS.toFloat(), true)

        HexUtils.iterateHex(x, y, RADIUS) {
            val build = it?.build ?: return@iterateHex
            if (!it.breakable() || it.block() is CoreBlock || it.team() != defender) return@iterateHex

            if (Math.random() < 0.2) {

                Core.app.post {
                    Call.effect(Fx.explosion, it.x.toFloat(), it.y.toFloat(), 90f, Color.white)
                }
                build.kill()
            }
        }
    }

    // region progress

    fun updateProgress() {
        if (hasCore) return
        Arrays.fill(progress.items, 0)

        HexUtils.iterateHex(
            x, y, RADIUS,
            { it?.build != null },
            { it?.let { progress.incr(it.team().id, it.block().buildTime.toInt()) } })
    }

    fun getProgress(team: Team) = progress.get(team.id)

    fun getProgressPercent(team: Team) =
        Mathf.floor(progress.get(team.id).toFloat() / Config.PROGRESS_REQUIREMENT * 100)

    // endregion
    // region geometry

    override fun getX() = (x * Vars.tilesize).toFloat()

    override fun getY() = (y * Vars.tilesize).toFloat()

    fun contains(position: Position) = contains(position.x, position.y)

    fun contains(x: Float, y: Float) =
        Intersector.isInsideHexagon(getX(), getY(), (RADIUS * 2 * Vars.tilesize).toFloat(), x, y)

    // endregion
}