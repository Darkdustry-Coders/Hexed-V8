package hexed.managers

import arc.func.Boolf
import arc.func.Cons
import arc.func.Intc2
import arc.math.geom.Position
import arc.struct.Seq
import hexed.structures.Shape
import mindurka.api.on
import arc.util.Log
import mindustry.game.EventType.PickupEvent
import mindustry.game.EventType.PayloadDropEvent
import mindustry.game.EventType.BlockBuildEndEvent
import mindustry.game.EventType.BlockDestroyEvent
import mindustry.game.Rules
import mindustry.world.blocks.ConstructBlock.ConstructBuild
import mindustry.world.blocks.storage.CoreBlock.CoreBuild
import kotlin.Unit as Nada
import mindustry.world.Tile
import mindustry.world.Tiles

lateinit var Shapes: ShapesImpl
interface ShapesImpl: Manager {
    val tlKey: String
    val shapes: Seq<out Shape>

    fun getShape(position: Position): Shape?
    fun getShapes(cons: Intc2)
    fun iterateShape(x: Int, y: Int, radius: Int, cons: Cons<Tile?>) {
        iterateShape(x, y, radius, { true }, cons)
    }
    fun iterateShape(x: Int, y: Int, radius: Int, filter: Boolf<Tile?>, cons: Cons<Tile?>)

    fun carvePaths(tiles: Tiles)

    fun applyRules(rules: Rules) {}
}

fun initShapes() {
    on { it: BlockBuildEndEvent ->
        if (it.tile.build == null) return@on
        val hex = Shapes.getShape(it.tile) ?: return@on

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
        val hex = Shapes.getShape(it.tile) ?: return@on

        it.tile.build?.let { build ->
            if (build is ConstructBuild) return@let
            hex.blockDestroyed(build.block, build.team);
        }
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
        val hex = Shapes.getShape(build.tile) ?: return@on
        hex.blockDestroyed(build.block, build.team);
    } }

    on { it: PickupEvent -> it.build?.let { build ->
        val hex = Shapes.getShape(build.tile) ?: return@on
        hex.blockCreated(build.block, build.team);
    } }
}