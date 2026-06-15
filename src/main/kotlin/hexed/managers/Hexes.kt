package hexed.managers

import arc.math.geom.Position
import arc.struct.Seq
import arc.util.Log
import hexed.structures.Hex
import hexed.utils.HexUtils.getHexes
import mindurka.api.on
import mindustry.game.EventType.PickupEvent
import mindustry.game.EventType.PayloadDropEvent
import mindustry.game.EventType.BlockBuildEndEvent
import mindustry.game.EventType.BlockDestroyEvent
import mindustry.world.blocks.ConstructBlock.ConstructBuild
import mindustry.world.blocks.storage.CoreBlock.CoreBuild
import kotlin.Unit as Nada

object Hexes : Manager {
    val hexes: Seq<Hex> = Seq()

    init {
        on { it: BlockBuildEndEvent ->
            if (it.tile.build == null) return@on
            val hex = getHex(it.tile) ?: return@on

            it.tile.build?.let { build ->
                val block = if (build is ConstructBuild) build.current else build.block
                if (!it.breaking && build is ConstructBuild)
                    Log.warn("Literally how the fuck (not breaking && build = ConstructBuild)")
                if (it.breaking) hex.blockDestroyed(block, build.team)
                else hex.blockCreated(block, build.team);
                Nada
            } ?: Log.warn("Created a non-building")
            // hex.requestUpdate() // Update hex controller
        }

        // on { it: CoreChangeEvent ->
        //     val hex = getHex(it.core) ?: return@on

        //     // hex.requestUpdate() // Update hex controller
        // }

        on { it: BlockDestroyEvent ->
            val hex = getHex(it.tile) ?: return@on

            it.tile.build?.let { build ->
                if (build is ConstructBuild) return@let
                hex.blockDestroyed(build.block, build.team);
                Nada
            } ?: Log.warn("Destroyed a non-building")
            // hex.requestUpdate() // Update hex controller

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

        on { it: PayloadDropEvent -> it.build?.let { build ->
            val hex = getHex(build.tile) ?: return@on
            hex.blockDestroyed(build.block, build.team);
        } }

        on { it: PickupEvent -> it.build?.let { build ->
            val hex = getHex(build.tile) ?: return@on
            hex.blockCreated(build.block, build.team);
        } }
    }

    override fun play() {
        getHexes { x, y -> hexes.add(Hex(x, y, hexes.size)) }
    }

    override fun reset() {
        hexes.clear()
    }

    // fun update() {
    //     hexes.each { it.update() }
    // }

    fun getHex(position: Position): Hex? = hexes.find { it.contains(position) }
}