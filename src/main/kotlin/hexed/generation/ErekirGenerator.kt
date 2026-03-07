package hexed.generation

import arc.func.Cons
import arc.struct.Seq
import hexed.Config
import hexed.utils.HexUtils
import mindustry.content.Blocks
import mindustry.content.Planets
import mindustry.game.Rules
import mindustry.maps.filters.ClearFilter
import mindustry.maps.filters.GenerateFilter
import mindustry.maps.filters.GenerateFilter.GenerateInput
import mindustry.maps.filters.NoiseFilter
import mindustry.world.Block
import mindustry.world.Tile

import hexed.Config.RADIUS
import java.util.BitSet

class ErekirGenerator : Generator {
    constructor(name: String, filler: Block, vararg filters: GenerateFilter) : super(
        name, Planets.erekir, Config.erekirBase, { rules -> Config.erekirRules.get(rules) }, filler, *filters
    )

    constructor(name: String, ruleSetter: Cons<Rules>, filler: Block, vararg filters: GenerateFilter) : super(
        name, Planets.erekir, Config.erekirBase, { rules ->
            ruleSetter.get(rules)
            Config.erekirRules.get(rules)
        }, filler, *filters
    )

    override fun generateLandscape(input: GenerateInput) {
        generateVents()
        super.generateLandscape(input)
    }

    override fun generateOres(input: GenerateInput) {
        applyFilters(
            input, *getOreFilters(
                -0.04f, 4f,
                Blocks.oreBeryllium,
                Blocks.oreTungsten,
                Blocks.oreCrystalThorium
            )
        )

        applyFilters(
            input, *getOreFilters(
                -0.08f, 2f,
                Blocks.wallOreBeryllium,
                Blocks.wallOreTungsten,
                Blocks.wallOreThorium
            )
        )

        applyFilters(
            input,
            NoiseFilter().apply {
                scl = 10f
                threshold = 0.6f
                floor = Blocks.air
                block = Blocks.graphiticWall
            },

            ClearFilter().apply {
                target = Blocks.graphiticWall
                replace = Blocks.carbonStone
            })
    }

    fun generateVents() {
        HexUtils.getHexes { x, y ->
            val tiles = Seq<Tile>()

            HexUtils.iterateHex(x, y, RADIUS - 4, { tile ->
                Decorations.vents.containsKey(tile?.floor())
            }, tiles::add)

            val occupied = BitSet(RADIUS * RADIUS * 4)

            for (i in 0 until minOf(tiles.size, 6)) {
                val tile = tiles.random()

                if (occupied[tile.x + tile.y * RADIUS * 2]) continue

                val vent = Decorations.vents[tile.floor()] ?: continue

                HexUtils.iterateNearby(tile) { other ->
                    if (other == null) return@iterateNearby

                    occupied.set(other.x + other.y * RADIUS * 2)
                    other.setFloor(vent.asFloor())
                }
            }
        }
    }
}