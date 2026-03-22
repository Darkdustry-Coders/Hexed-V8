package hexed.generation.filters

import mindustry.maps.filters.ScatterFilter

class SolidScatterFilter : ScatterFilter() {

    override fun apply(input: GenerateInput) {
        if ((!flooronto.isAir && input.floor != flooronto) || chance(input.x, input.y) > chance) return

        if (!block.isAir && !input.block.isAir) {
            if (block.isOverlay) {
                input.overlay = block
            } else {
                input.block = block
            }
        }

        if (!floor.isAir) {
            input.floor = floor
        }
    }
}