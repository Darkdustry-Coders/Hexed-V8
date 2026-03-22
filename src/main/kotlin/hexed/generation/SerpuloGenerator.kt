package hexed.generation

import arc.func.Cons
import hexed.Config
import mindustry.content.Blocks
import mindustry.content.Planets
import mindustry.game.Rules
import mindustry.maps.filters.GenerateFilter
import mindustry.maps.filters.GenerateFilter.GenerateInput
import mindustry.world.Block

class SerpuloGenerator : Generator {
    constructor(name: String, filler: Block, vararg filters: GenerateFilter) : super(
        name, Planets.serpulo, Config.serpuloBase, { rules -> Config.serpuloRules.get(rules) }, filler, *filters
    )

    constructor(name: String, ruleSetter: Cons<Rules>, filler: Block, vararg filters: GenerateFilter) : super(
        name, Planets.serpulo, Config.serpuloBase, { rules ->
            ruleSetter.get(rules)
            Config.serpuloRules.get(rules)
        },
        filler,
        *filters
    )

    override fun generateOres(input: GenerateInput) {
        applyFilters(input, *getOreFilters(
                -0.04f,
                8f,
                Blocks.oreCopper,
                Blocks.oreLead,
                Blocks.oreScrap,
                Blocks.oreCoal,
                Blocks.oreTitanium,
                Blocks.oreThorium
            ),
        )
    }
}