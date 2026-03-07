package hexed.utils

import arc.func.Boolf
import arc.func.Cons
import arc.func.Intc2
import arc.math.Mathf
import arc.math.geom.Intersector
import hexed.Config
import mindustry.Vars
import mindustry.world.Tile
import mindustry.world.blocks.environment.SteamVent
import hexed.Config.SPACING

object HexUtils {
    fun iterateHex(x: Int, y: Int, radius: Int, cons: Cons<Tile?>) {
        iterateHex(x, y, radius, { true }, cons)
    }

    fun iterateHex(x: Int, y: Int, radius: Int, filter: Boolf<Tile?>, cons: Cons<Tile?>) {
        for (cx in -radius..radius) {
            for (cy in -radius..radius) {
                if (!Intersector.isInsideHexagon(
                        x.toFloat(),
                        y.toFloat(),
                        (radius * 2).toFloat(),
                        (x + cx).toFloat(),
                        (y + cy).toFloat()
                    )
                ) continue

                val tile = Vars.world.tile(x + cx, y + cy) ?: return
                if (filter.get(tile)) cons.get(tile)
            }
        }
    }

    fun iterateNearby(tile: Tile, cons: Cons<Tile?>) {
        for (point in SteamVent.offsets) {
            val nearby = tile.nearby(point) ?: return

            cons.get(nearby)
        }
    }

    fun getHexes(cons: Intc2) {
        val height = Mathf.sqrt3 * SPACING / 4f

        for (x in 0 until Config.WIDTH / SPACING - 2) {
            for (y in 0 until  Mathf.ceil(Config.HEIGHT / height - 2)) {
                val cx = (x * SPACING * 1.5f + (y % 2) * SPACING * 0.75f) + SPACING / 2
                val cy = (y * height) + SPACING / 2

                cons.get(cx.toInt(), cy.toInt())
            }
        }
    }
}