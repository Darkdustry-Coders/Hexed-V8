package hexed.generation.filters

import mindustry.maps.filters.OreFilter

class WallOreFilter : OreFilter() {

    var wallOre: Boolean = false

    override fun apply(input: GenerateInput) {
        if (wallOre != input.block.solid && input.floor.asFloor().hasSurface().not()) return

        val noise = noise(input.x.toFloat(), input.y + input.x * tilt, scl, 1f, octaves, falloff)
        if (noise <= threshold) return

        if (target.isAir || input.floor == target || input.block == target || input.overlay == target)
            input.overlay = ore
    }
}