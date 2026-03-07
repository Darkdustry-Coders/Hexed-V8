package hexed.generation.filters

import arc.math.Mathf
import arc.struct.Seq
import hexed.Config
import hexed.utils.HexUtils
import mindustry.content.Blocks
import mindustry.maps.filters.NoiseFilter
import mindustry.world.Block

class LakesNoiseFilter : NoiseFilter() {

    var minRadius: Int = 25
    var maxRadius: Int = Config.RADIUS

    var targets: Seq<Block?> = Seq()

    override fun apply(input: GenerateInput) {
        if (!(targets.isEmpty || targets.contains(input.block) || targets.contains(input.floor) || targets.contains(input.overlay))) {
            return
        }

        HexUtils.getHexes { x, y ->
            val dst = Mathf.dst(x.toFloat(), y.toFloat(), input.x.toFloat(), input.y.toFloat())
            if (dst > minRadius && dst < maxRadius) {
                val noise = noise(input.x.toFloat(), input.y + input.x * tilt, scl, 1f, octaves, falloff)
                if (noise > threshold) {
                    if (floor != Blocks.air) input.floor = floor
                    if (block != Blocks.air && input.block != Blocks.air && !input.block.breakable) {
                        input.block = block
                    }
                }
            }
        }
    }
}