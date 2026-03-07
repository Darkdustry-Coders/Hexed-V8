package hexed.coreplugin

import arc.files.Fi
import arc.struct.Seq
import hexed.Config
import hexed.generation.Generator
import hexed.generation.Generators
import hexed.managers.Game

import mindurka.api.MapFlags
import mindurka.api.MapHandle
import mindurka.api.MapManager
import mindurka.util.SafeFilename
import mindurka.util.child
import mindurka.util.map
import mindustry.Vars
import mindustry.game.MapObjectives
import mindustry.io.SaveIO
import mindustry.io.SaveMeta
import java.util.WeakHashMap

open class HexMapManager : MapManager {
    private val mapHandles = WeakHashMap<Generator, MapHandle>()
    private fun mapHandleFor(map: Generator): MapHandle {
        val handle = mapHandles[map]
        if (handle != null) return handle
        val newHandle = HexMapHandle(map)
        mapHandles[map] = newHandle
        return newHandle
    }

    protected companion object {
        var nextMap: MapHandle? = null
    }

    open class HexMapHandle(val map: Generator) : MapHandle {
        val flags = Seq<MapFlags>()

        override fun name(): String = map.name
        override fun description(): String = ""
        override fun author(): String = "Mindurka Project"
        override fun width(): Int = Config.WIDTH
        override fun height(): Int = Config.HEIGHT
        override fun flags(): Iterator<MapFlags> = flags.iterator()
        override fun rtv() {
            // Called immediately after the RTV vote ends
            Game.rtv(map)
        }
    }

    // Probably never saving on Hexed; just copied from CorePlugin, lol
    open class DefaultSaveHandle(val fi: Fi, val meta: SaveMeta, val flags: Seq<MapFlags>) : MapHandle {
        override fun name(): String = meta.map.name()
        override fun description(): String = meta.map.description()
        override fun author(): String = meta.map.author()
        override fun width(): Int = meta.map.width
        override fun height(): Int = meta.map.height
        override fun flags(): Iterator<MapFlags> = flags.iterator()
        override fun rtv() {
            SaveIO.load(fi)
        }
    }

    protected val saves = Seq<DefaultSaveHandle>()

    protected fun reloadSaves() {
        val dir = Vars.dataDirectory.child("saves")
        if (!dir.exists()) return
        saves.clear()
        for (save in dir.list()) {
            try {
                val meta = SaveIO.getMeta(save)
                val flags = Seq<MapFlags>()
                for (obj in meta.map.rules().objectives) {
                    if (obj is MapObjectives.FlagObjective) {
                        if (obj.details == "no1va" || obj.flag == "no1va" || obj.text() == "no1va") {
                            flags.addUnique(MapFlags.No1va)
                            continue
                        }
                    }
                }
                saves.add(DefaultSaveHandle(save, meta, flags))
            } catch (_: Exception) {
            }
        }
    }

    init {
        reloadSaves()
    }

    override fun current(): MapHandle = mapHandleFor(Game.generator)
    override fun maps(): Iterator<MapHandle> =
        Generators.generators.iterator().map {
            mapHandleFor(it)
        }

    override fun saves(): Iterator<MapHandle> = saves.iterator()
    override fun hasSaves(): Boolean = false
    override fun next(): MapHandle {
        val nextMap = nextMap
        if (nextMap != null) {
            HexMapManager.nextMap = null
            return nextMap
        }

        val map = Generators.random(null)
        return mapHandleFor(map)
    }

    override fun setNext(map: MapHandle) {
        nextMap = map
    }

    override fun save(name: SafeFilename) {
        Vars.dataDirectory.child("saves").mkdirs()
        SaveIO.write(Vars.dataDirectory.child("saves").child(name))
    }

    override fun refresh() {

    }
}