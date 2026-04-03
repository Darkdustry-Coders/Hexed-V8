package hexed.generation

import arc.func.Cons
import arc.struct.Seq
import hexed.Config
import hexed.utils.HexUtils
import mindustry.content.Blocks
import mindustry.content.Planets
import mindustry.game.Rules
import mindustry.maps.filters.GenerateFilter
import mindustry.maps.filters.GenerateFilter.GenerateInput
import mindustry.maps.filters.NoiseFilter
import mindustry.world.Block
import mindustry.world.Tile

import hexed.Config.RADIUS
import hexed.generation.filters.SolidNoiseFilter
import java.util.BitSet

class MixtechGenerator : Generator {
    constructor(name: String, filler: Block, vararg filters: GenerateFilter) : super(
        name, Planets.sun, Config.mixtechBase, { rules -> Config.mixtechRules.get(rules) }, filler, *filters
    )

    constructor(name: String, ruleSetter: Cons<Rules>, filler: Block, vararg filters: GenerateFilter) : super(
        name, Planets.sun, Config.mixtechBase, { rules ->
            ruleSetter.get(rules)
            Config.mixtechRules.get(rules)
        }, filler, *filters
    )

    override fun generateLandscape(input: GenerateInput) {
        generateVents()
        super.generateLandscape(input)
    }

    override fun generateOres(input: GenerateInput) {
        applyFilters(
            input,
            SolidNoiseFilter().apply {
                scl = 25f
                threshold = 0.87f
                octaves = 10f
                falloff = 0f
                block = Blocks.duneWall
            },

            *getOreFilters(
                +0.03f, 0f,
                Blocks.oreCrystalThorium,
                Blocks.oreTungsten,
                Blocks.oreBeryllium
            ),

            *getOreFilters(
                -0.14f, -5f,
                Blocks.wallOreThorium,
                Blocks.wallOreTungsten,

                ),

            NoiseFilter().apply {
                scl = 20f
                threshold = 0.6f
                floor = Blocks.air
                block = Blocks.graphiticWall
            },

            *getOreFilters(
                -0.14f, -5f,
                Blocks.wallOreBeryllium
            ),

            *getOreFilters(
                -0.04f,
                8f,
                Blocks.oreCopper,
                Blocks.oreLead,
                Blocks.oreCoal,
                Blocks.oreTitanium,
            ),
        )

    }

    fun generateVents() {
        HexUtils.getHexes { x, y ->
            val tiles = Seq<Tile>()

            HexUtils.iterateHex(x, y, RADIUS - 4, { tile ->
                Decorations.vents.containsKey(tile?.floor())
            }, tiles::add)

            val occupied = BitSet(RADIUS * RADIUS * 4)

            for (i in 0 until minOf(tiles.size, 4)) {
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