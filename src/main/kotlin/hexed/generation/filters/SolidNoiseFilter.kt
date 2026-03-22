package hexed.generation.filters

import mindustry.maps.filters.NoiseFilter

class SolidNoiseFilter : NoiseFilter() {

    override fun apply(input: GenerateInput) {
        val noise = noise(input.x.toFloat(), input.y + input.x * tilt, scl, 1f, octaves, falloff)

        if (noise > threshold) {
            input.block = block
            input.floor = floor
        }
    }
}