package hexed.generation

import arc.struct.ObjectMap
import mindustry.content.Blocks
import mindustry.world.Block
import mindustry.world.blocks.environment.Floor

object Decorations {
    var props: ObjectMap<Floor, Block> = ObjectMap.of(
        Blocks.moss, Blocks.sporeCluster,
        Blocks.sporeMoss, Blocks.sporeCluster
    )

    var vents: ObjectMap<Floor, Block> = ObjectMap.of(
        Blocks.rhyolite, Blocks.rhyoliteVent,
        Blocks.beryllicStone, Blocks.arkyicVent,
        Blocks.arkyicStone, Blocks.arkyicVent,
        Blocks.yellowStone, Blocks.yellowStoneVent,
        Blocks.yellowStonePlates, Blocks.yellowStoneVent,
        Blocks.regolith, Blocks.yellowStoneVent,
        Blocks.redStone, Blocks.redStoneVent,
        Blocks.denseRedStone, Blocks.redStoneVent,
        Blocks.carbonStone, Blocks.carbonVent
    )
}