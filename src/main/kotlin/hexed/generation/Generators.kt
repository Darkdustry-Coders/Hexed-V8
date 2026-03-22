package hexed.generation

import arc.struct.Seq
import hexed.generation.filters.LakesNoiseFilter
import mindustry.maps.filters.NoiseFilter
import mindustry.maps.filters.BlendFilter
import mindustry.content.Blocks.*
import mindustry.maps.filters.ScatterFilter

// The best way to generate maps
// Ultra compact
object Generators {
    val tarFields = SerpuloGenerator(
        "Tar Fields", sand,

        NoiseFilter().apply {
            floor = shale
            block = shaleWall
            scl = 200f
            threshold = 0.56f
            octaves = 10f
            falloff = 0.7f
            tilt = 0.5f
        },

        NoiseFilter().apply {
            floor = darksandWater
            target = shale
            scl = 20f
            threshold = 0.63f
            octaves = 10f
            falloff = 0.71f
            tilt = 0.32f
        },

        LakesNoiseFilter().apply {
            scl = 40f
            threshold = 0.82f
            floor = tar
        },

        BlendFilter().apply {
            block = tar
            floor = charr
            radius = 1f
        }
    )

    val volcano = SerpuloGenerator(
        "Volcano", darksand,

        NoiseFilter().apply {
            floor = hotrock
            block = air
            target = darksand
            scl = 127.2f
            threshold = 0.645f
            octaves = 6.48f
            falloff = 0.845f
            tilt = 0.44f
        },

        NoiseFilter().apply {
            floor = hotrock
            block = air
            target = darksand
            scl = 134.7f
            threshold = 0.705f
            octaves = 6.17f
            falloff = 0.845f
            tilt = -4f
        },

        NoiseFilter().apply {
            floor = hotrock
            block = air
            target = darksand
            scl = 134.7f
            threshold = 0.705f
            octaves = 6.17f
            falloff = 0.845f
            tilt = 4f
        },

        BlendFilter().apply {
            floor = basalt
            block = hotrock
            radius = 2.34f
        },

        NoiseFilter().apply {
            floor = magmarock
            block = air
            target = hotrock
            scl = 69.86f
            threshold = 0.525f
            octaves = 5.4f
            falloff = 0.975f
            tilt = 0.64f
        },

        LakesNoiseFilter().apply {
            floor = slag
            block = air
            targets.add(magmarock)
            minRadius = 20
            scl = 69.86f
            threshold = 0.525f
            octaves = 5.4f
            falloff = 0.975f
            tilt = 0.64f
        }
    )

    val spores = SerpuloGenerator(
        "Spores", darksand,
        NoiseFilter().apply {
            floor = moss
            block = sporeWall
            threshold = 0.55f
            falloff = 0.7f
            scl = 160f
            octaves = 10f
        },

        NoiseFilter().apply {
            floor = shale
            block = shaleWall
            target = moss
            falloff = 0.7f
            scl = 80f
            threshold = 0.53f
            octaves = 7f
        },

        NoiseFilter().apply {
            floor = sporeMoss
            block = sporeWall
            target = moss

            threshold = 0.6f
            falloff = 0.5f
            scl = 40f
            octaves = 10f
        },

        LakesNoiseFilter().apply {
            floor = darksandTaintedWater
            scl = 15f
            threshold = 0.76f
            minRadius = 0
        }
    )

    val winter = SerpuloGenerator(
        "Winter", darksand,

        NoiseFilter().apply {
            floor = mud
            block = dirtWall
            target = dirt
            threshold = 0.7f
            falloff = 0.7f
            scl = 160f
            octaves = 10f
        },

        NoiseFilter().apply {
            floor = snow
            block = snowWall
            threshold = 0.6f
            falloff = 0.65f
            scl = 220f
            octaves = 10f
            tilt = 0.9f
        },

        NoiseFilter().apply {
            floor = ice
            block = iceWall
            target = snow
            threshold = 0.6f
            falloff = 0.5f
            scl = 10f
            octaves = 10f
        },

        NoiseFilter().apply {
            floor = iceSnow
            block = iceWall
            target = snow
            threshold = 0.6f
            falloff = 0.5f
            scl = 10f
            octaves = 10f
        },

        NoiseFilter().apply {
            floor = dacite
            block = stoneWall
            threshold = 0.7f
            falloff = 0.6f
            scl = 60f
            octaves = 10f
            tilt = 1.1f
        },

        NoiseFilter().apply {
            floor = darksandTaintedWater
            block = stoneWall
            scl = 30f
            threshold = 0.75f
            octaves = 5f
            falloff = 0.7f
            tilt = 1f
        },

        LakesNoiseFilter().apply {
            scl = 40f
            threshold = 0.82f
            floor = cryofluid
        }
    )

    val erekir = ErekirGenerator(
        "Erekir", darksand,

        ScatterFilter().apply {
            chance = 1f
            floor = carbonStone.asFloor()
        },

        LakesNoiseFilter().apply {
            minRadius = 10
            scl = 30f
            threshold = 0.92f
            falloff = 0f
            octaves = 10f
            floor = arkyciteFloor
            block = air
        },

        LakesNoiseFilter().apply {
            minRadius = 10
            scl = 30f
            threshold = 0.92f
            falloff = 0f
            octaves = 10f
            floor = slag
            block = air
        },
    )

    val generators: Seq<Generator> = Seq<Generator>().addAll(tarFields, volcano, spores, winter, erekir)

    fun random(previous: Generator?): Generator {
        return generators.random(previous)
    }

    fun findByName(name: String): Generator? {
        return try {
            val index = name.toInt()
            if (index in 1..generators.size) generators.get(index - 1)
            else null
        } catch (_: NumberFormatException) {
            generators.find { generator: Generator -> generator.name.equals(name, ignoreCase = true) }
        }
    }

}


