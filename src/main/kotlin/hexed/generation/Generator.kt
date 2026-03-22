package hexed.generation

import arc.func.Cons
import arc.math.Mathf
import arc.math.geom.Bresenham2
import arc.math.geom.Geometry
import arc.struct.StringMap
import arc.util.Tmp
import hexed.Config
import hexed.Config.RADIUS
import hexed.utils.HexUtils
import hexed.generation.filters.WallOreFilter
import mindustry.Vars
import mindustry.content.Blocks
import mindustry.content.Planets
import mindustry.game.Rules
import mindustry.game.Schematic
import mindustry.maps.Map
import mindustry.maps.filters.GenerateFilter
import mindustry.maps.filters.GenerateFilter.GenerateInput
import mindustry.maps.filters.OreFilter
import mindustry.type.Planet
import mindustry.world.Block
import mindustry.world.Tile
import mindustry.world.Tiles
import mindustry.world.blocks.ConstructBlock
import mindustry.world.blocks.environment.OreBlock

abstract class Generator(
    val name: String,
    val planet: Planet,
    val startBase: Schematic,
    val ruleSetter: Cons<Rules>,
    val filler: Block,
    vararg val filters: GenerateFilter
) {

    fun generate(tiles: Tiles) {
        // First, fill the entire map
        tiles.each { x, y -> tiles.set(x, y, Tile(x, y, filler, Blocks.air, filler.asFloor().wall)) }

        // Then carve out the hexes
        HexUtils.getHexes { x, y ->
            // Remove the main hex area
            HexUtils.iterateHex(x, y, RADIUS) { it?.remove() }

            // Carve the hex edges
            for (angle in 0 until 360 step 120) {
                Tmp.v1.trnsExact(angle - 30f, 90f).add(x.toFloat(), y.toFloat())

                if (!tiles.`in`(Tmp.v1.x.toInt(), Tmp.v1.y.toInt())) continue

                Tmp.v1.trnsExact(angle - 30f, 46f).add(x.toFloat(), y.toFloat())

                Bresenham2.line(x, y, Tmp.v1.x.toInt(), Tmp.v1.y.toInt()) { cx, cy ->
                    Geometry.circle(cx, cy, tiles.width, tiles.height, 3) { c2x, c2y ->
                        tiles.getc(c2x, c2y)?.remove()
                    }
                }
            }
        }

        val input = GenerateInput()

        applyRules(Vars.state.rules)
        applyFilters(input, *filters)

        generateOres(input)
        generateLandscape(input)
        generateDecorations()
        generateCore()
        postGenerate()

        Vars.state.map = Map(StringMap.of("name", name))
    }

    fun generateCore() {
        val radius = if (this.planet == Planets.serpulo) 5 else 7 // nice

        HexUtils.getHexes { x, y ->
            Vars.world.tile(x, y)?.getLinkedTilesAs(ConstructBlock.get(radius)) {
                it?.remove()
                it?.setFloor(Blocks.coreZone.asFloor())
            }
        }
    }

    open fun generateOres(input: GenerateInput) {

    }

    open fun generateLandscape(input: GenerateInput) {

    }

    fun generateDecorations() {
        Vars.world.tiles.eachTile {
            if (!Mathf.chance(0.01) || it.solid() || it.floor().isLiquid) return@eachTile
            it.setBlock(Decorations.props.get(it.floor(), it.floor().decoration))
        }
    }

    fun applyFilters(input: GenerateInput, vararg filters: GenerateFilter) {
        input.begin(Vars.world.width(), Vars.world.height()) { x, y -> Vars.world.tile(x, y) }

        filters.forEach {
            it.randomize()
            it.apply(Vars.world.tiles, input)
        }
    }

    fun applyRules(rules: Rules) {
        planet.applyRules(rules)
        Config.defaultRules.get(rules) // First apply default rules
        ruleSetter.get(rules) // Then apply custom rules
    }

    fun postGenerate() {
        Vars.world.tiles.eachTile {
            if (it.floor().isLiquid) it.remove()
            if (it.block().itemDrop != null) it.clearOverlay()
        }
    }

    fun getOreFilters(
        oreThreshold: Float, oreScale: Float, vararg ores: Block
    ): Array<OreFilter> {
        return ores.filterIsInstance<OreBlock>().map { block ->
            val filter = if (block.wallOre) {
                WallOreFilter()
            } else {
                OreFilter()
            }

            filter.apply {
                threshold = block.oreThreshold + oreThreshold
                scl = block.oreScale + oreScale
                ore = block
            }
        }.toTypedArray()
    }
}