package hexed.managers

import arc.func.Boolf
import arc.func.Cons
import arc.func.Intc2
import arc.math.geom.Bresenham2
import arc.math.geom.Geometry
import arc.math.geom.Position
import arc.struct.Seq
import arc.util.Log
import arc.util.Tmp
import hexed.Config
import hexed.structures.Octet
import hexed.structures.Shape
import mindustry.Vars
import mindustry.game.Rules
import mindustry.world.Tile
import mindustry.world.Tiles
import kotlin.math.absoluteValue

object Octets: ShapesImpl {
    override val tlKey: String = "octet"

    private val octets = Seq<Octet>(true, 0, Octet::class.java)
    override val shapes: Seq<out Shape> get() = octets

    override fun play() {
        getShapes { x, y -> octets.add(Octet(x, y, octets.size)) }
    }

    override fun reset() {
        octets.clear()
    }

    fun getOctet(position: Position): Octet? = octets.find { it.contains(position) }
    override fun getShape(position: Position): Shape? = getOctet(position)

    override fun getShapes(cons: Intc2) {
        for (x in 0 until Config.WIDTH / Config.SPACING - 1) for (y in 0 until Config.HEIGHT / Config.SPACING - 1) {
            cons.get((x + 1) * Config.SPACING, (y + 1) * Config.SPACING)
        }
    }

    override fun iterateShape(x: Int, y: Int, radius: Int, filter: Boolf<Tile?>, cons: Cons<Tile?>) {
        val r1 = Config.RADIUS
        val r2 = (Config.RADIUS * 1.4f).toInt()
        for (cx in -r1..r1) for (cy in -r1..r1) {
            if (cx.absoluteValue + cy.absoluteValue > r2) continue
            val tile = Vars.world.tile(x + cx, y + cy) ?: continue
            if (filter[tile]) cons[tile]
        }
    }

    override fun carvePaths(tiles: Tiles) {
        Shapes.getShapes { x, y ->
            for (angle in 0 until 360 step 90) {
                Tmp.v1.trnsExact(angle - 0f, 120f).add(x.toFloat(), y.toFloat())

                if (!tiles.`in`(Tmp.v1.x.toInt(), Tmp.v1.y.toInt())) continue

                Tmp.v1.trnsExact(angle - 0f, 46f).add(x.toFloat(), y.toFloat())

                Bresenham2.line(x, y, Tmp.v1.x.toInt(), Tmp.v1.y.toInt()) { cx, cy ->
                    Geometry.circle(cx, cy, tiles.width, tiles.height, 3) { c2x, c2y ->
                        tiles.getc(c2x, c2y)?.remove()
                    }
                }
            }
        }
    }

    override fun applyRules(rules: Rules) {
        rules.enemyCoreBuildRadius *= 1.08f // no, this is not an arbitrary number.
    }
}