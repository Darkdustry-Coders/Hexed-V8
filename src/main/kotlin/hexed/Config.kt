package hexed

import arc.func.Cons
import arc.struct.Seq
import mindustry.Vars
import mindustry.content.Blocks
import mindustry.content.Items
import mindustry.game.Rules
import mindustry.game.Schematic
import mindustry.game.Schematics
import mindustry.type.ItemStack

object Config {
    const val WIDTH = 528
    const val HEIGHT = 516
    const val SPACING = 84
    const val RADIUS = 40
    const val WIN_HEXES = 52

    const val PROGRESS_REQUIREMENT = 3000f
    const val ROUND_TIME = 120 * 60 * 60f
    const val CHECK_WIN_TIME = 1 * 5 * 60f
    const val LEFT_TEAM_DESTROY_TIME = 3 * 60 * 60f
    const val HUD_TIME = 1 * 1 * 60f
    const val LEADERBOARD_TIME = 2 * 60 * 60f

    val serpuloBase: Schematic = Schematics.readBase64(
        "bXNjaAF4nE2SW1LDMAxFZSd2/EgpXUhWxPBhUgOdSeNM0vLYOj8g+ZaBpOm1LOlYlk2RDg21czpnMq/5Ix8pHvM2rqflciozEdkpPeVpI/3wuKM4lmXJ6/CepokO/4xhSutLJjeW+S1/lpW6bUyXS14pboV9w5LmPFGT1pG6p+r5pMM/1y/gOk8lHTmvH8uah/k6Tvm6kT3nWWbDUt55ybkcM8V0WofnNF4Ks4gyf6TqjzS//LQQA7GkRTuqpoN4qk+AREgPyg7WXgIVxkoGDf+1mKxMBaYCU4GphNmyREiPyRvsnrT6K45fBjG3ll6ZGkwNpgZTC7PjuJ605N0JSgvT8qSWyuTlnIaMYaf2zHey9QbBLRpRqw8sBtad2C2Ka6U4i7oCS0M1jlMii3HSCVvHUcbfX1rcPYJ3wjNYy4Bn0TkrnbOyOdIdb4J5jq0oZXJ2Rzt162+H8E6SHYuE+Dq/l207nK5DnySgioN4+GrvHc7zdsQel0MCuGIvBYjUy+EB84B5wDxgHjCPg/Q4SC9bFNkj5B4NVbhLt/YaSEUHoAPQAeiAexdQZwA64N4FrBBkhR8RWUj7"
    )
    val erekirBase: Schematic = Schematics.readBase64(
        "bXNjaAF4nGNgZWBlZmDJS8xNZWC72HCx+WI7A3dKanFyUWZBSWZ+HgMDA1tOYlJqTjEDU3QsIwNPcn5Rqm5yZkliSmoOUJKRgYEJCBkA4IsSVg=="
    )
    val mixtechBase: Schematic = Schematics.readBase64(
        "bXNjaAF4nGNgZWBlZmDJS8xNZWC72HCx+WI7A3dKanFyUWZBSWZ+HgMDA1tOYlJqTjEDU3QsIwNPcn5Rqm5yZkliSmoOUJKRgYEJCBkA4IsSVg=="
    )


    val serpuloLoadout: Seq<ItemStack> = ItemStack.list(
        Items.copper, 350,
        Items.lead, 250,
        Items.graphite, 150,
        Items.metaglass, 150,
        Items.silicon, 250,
        Items.titanium, 50
    )

    val erekirLoadout: Seq<ItemStack> = ItemStack.list(
        Items.beryllium, 500,
        Items.tungsten, 350,
        Items.graphite, 400,
        Items.silicon, 300
    )

    val mixtechLoadout: Seq<ItemStack> = ItemStack.list(

    )

    val defaultRules = Cons { it: Rules ->
        // Required by CorePlugin
        it.modeName = "hexed"
        it.tags.put("mdrk.format", "1")
        it.tags.put("mdrk.gamemode", "hexed")


        // For tests
        it.infiniteResources = false

        it.enemyCoreBuildRadius = (RADIUS * Vars.tilesize).toFloat()

        it.pvp = true
        it.waves = false
        it.pvpAutoPause = false
        it.canGameOver = false

        it.coreCapture = true
        it.coreDestroyClear = false

        it.reactorExplosions = false
        it.damageExplosions = true
        it.fire = true

        it.logicUnitBuild = true
        it.unitPayloadUpdate = true // Yes, quads with fuse

        it.fog = false
        it.staticFog = false
    }

    val erekirRules = Cons { it: Rules ->
        it.loadout = erekirLoadout
    }

    val serpuloRules = Cons { it: Rules ->
        it.loadout = serpuloLoadout
        it.buildSpeedMultiplier = 2f

        // Less scrap
        Blocks.oreScrap.asFloor().oreThreshold = 0.9f
        Blocks.oreScrap.asFloor().oreScale = 20f
    }

    val mixtechRules = Cons { it: Rules -> {

    }}

}


