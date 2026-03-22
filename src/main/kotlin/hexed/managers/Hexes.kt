package hexed.managers

import arc.Events
import arc.math.geom.Position
import arc.struct.Seq
import hexed.structures.Hex
import hexed.utils.HexUtils.getHexes
import mindustry.game.EventType.BlockBuildEndEvent
import mindustry.game.EventType.BlockDestroyEvent
import mindustry.game.EventType.CoreChangeEvent
import mindustry.world.blocks.storage.CoreBlock.CoreBuild

object Hexes : Manager {
    val hexes: Seq<Hex> = Seq()

    init {
        Events.on(BlockBuildEndEvent::class.java) {
            if (it.tile.build == null) return@on
            val hex = getHex(it.tile) ?: return@on

            hex.requestUpdate() // Update hex controller
        }

        Events.on(CoreChangeEvent::class.java) {
            val hex = getHex(it.core) ?: return@on

            hex.requestUpdate() // Update hex controller
        }

        Events.on(BlockDestroyEvent::class.java) {
            val hex = getHex(it.tile) ?: return@on

            hex.requestUpdate() // Update hex controller

            val core = it.tile.build as? CoreBuild ?: return@on
            val defender = Session.parties[core.team] ?: return@on
            val attacker = Session.parties[core.lastDamage]

            defender.lastDamage = attacker

            hex.destroy(defender.team, attacker?.team)
            if (core.team.cores().size == 1) {
                Session.destroy(defender)
                attacker?.kills++
            }
        }
    }

    override fun play() {
        getHexes { x, y -> hexes.add(Hex(x, y, hexes.size)) }
    }

    override fun reset() {
        hexes.clear()
    }

    fun update() {
        hexes.each { it.update() }
    }

    fun getHex(position: Position): Hex? = hexes.find { it.contains(position) }
}