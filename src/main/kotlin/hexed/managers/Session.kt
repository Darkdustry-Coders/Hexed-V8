package hexed.managers

import arc.Events
import arc.func.Cons
import arc.graphics.Color
import arc.math.Mathf
import arc.struct.ObjectMap
import arc.struct.Seq
import arc.util.Log
import arc.util.Time
import arc.util.Timer
import buj.tl.Tl.send
import hexed.Config
import hexed.structures.Hex
import hexed.structures.Member
import hexed.structures.Party
import mindustry.Vars
import mindustry.game.EventType
import mindustry.game.EventType.PlayerConnectionConfirmed
import mindustry.game.Team
import mindustry.game.Teams.TeamData
import mindustry.gen.Call
import mindustry.gen.Player
import mindustry.world.blocks.storage.CoreBlock.CoreBuild

object Session : Manager {
    val members: ObjectMap<String, Member> = ObjectMap()
    val tasks: ObjectMap<Member, Timer.Task> = ObjectMap()
    val parties: ObjectMap<Team, Party> = ObjectMap()

    init {
        Events.on(PlayerConnectionConfirmed::class.java) {
            connect(it.player)
        }

        Events.on(EventType.PlayerLeave::class.java) {
            disconnect(it.player)
        }
    }

    override fun init() {

    }

    override fun reset() {
        tasks.values().forEach { it.cancel() }
        tasks.clear()
        parties.clear()
        members.clear()
    }

    // region get/create

    fun getMember(player: Player): Member? =
        members.get(player.uuid())

    fun getParty(member: Member): Party? =
        parties.get(member.team)

    fun createMember(player: Player): Member =
        Member(player).also {
            members.put(player.uuid(), it)
        }

    fun createParty(leader: Member, team: Team): Party =
        Party(leader).also {
            leader.team(team)
            parties.put(team, it)
        }

    fun getLeaderboard(amount: Int): Seq<Party> =
        parties.values()
            .toSeq()
            .select { it.controlled > 0 }
            .sort { (-it.controlled).toFloat() }
            .also { it.truncate(amount) }

    // endregion

    fun destroy(party: Party) {
        parties.remove(party.team)

        party.team.cores().each(Cons {
            it.tile.removeNet()
            Hexes.getHex(it)?.requestUpdate()
        })

        val data: TeamData = party.team.data()
        data.players.each {
            it.clearUnit()
            it.team(Team.derelict)
            Call.hideHudText(it.con)
        }
        data.buildings.each {
            Time.run(Mathf.random(360f)) { Call.removeTile(it!!.tile) }
        }
        data.units.each {
            Time.run(Mathf.random(360f)) { Call.unitDespawn(it) }
        }
        data.plans.clear()

        party.destroy()
    }

    fun connect(player: Player) {
        var member = getMember(player)

        if (member == null) {
            member = createMember(player)
            spawn(member)
            return
        }

        val task = tasks[member]
        if (task != null) {
            member.reconnect(player)
            task.cancel()
            tasks.remove(member)
        }
    }

    fun disconnect(player: Player) {
        val member = getMember(player) ?: return

        if (member.isDerelict) {
            members.remove(member.player.uuid())
        }

        tasks.put(member, Time.runTask(Config.LEFT_TEAM_DESTROY_TIME) {
            leave(member)
            members.remove(member.player.uuid())
        })
    }

    fun leave(member: Member) {
        val party = getParty(member) ?: return

        if (party.isOne) {
            destroy(party)
        } else {
            party.leave(member)
        }
    }

    // region spawn
    fun spawn(member: Member) {
        val hex = getSpawnHex()
        val team = getSpawnTeam()

        if (hex == null || team == null) {
            send(member.player).done("{game.no-available-hex}")
            return
        }

        val schematic = Game.generator.startBase
        val x = hex.x - schematic.width / 2
        val y = hex.y - schematic.height / 2

        schematic.tiles.each { stile ->
            val tile = Vars.world.tile(stile.x + x, stile.y + y) ?: return@each

            tile.setNet(stile.block, team, stile.rotation.toInt())
            tile.build.configureAny(stile.config)

            if (tile.build is CoreBuild) {
                for (stack in Vars.state.rules.loadout) Call.setItem(
                    tile.build,
                    stack.item,
                    stack.amount
                )
            }
        }

        createParty(member, team)

        hex.update() // Update hex controller

        // Poor mobile players
        Call.setCameraPosition(member.player.con, hex.getX(), hex.getY())
    }

    fun getSpawnTeam(): Team? {
        fun colorDistance(a: Color, b: Color): Float {
            val dr = a.r - b.r
            val dg = a.g - b.g
            val db = a.b - b.b
            return dr * dr + dg * dg + db * db
        }

        val spawned = Team.all.filter { it.active() }
        val candidates = Team.all.filter { !it.active() && it != Team.derelict }

        if (spawned.isEmpty()) return candidates.random()

        return candidates.maxByOrNull { team: Team ->
            val contrast = spawned.sumOf { colorDistance(team.color, it.color).toDouble() }
            contrast + 1f
        }
    }

    fun getSpawnHex(): Hex? =
        Hexes.hexes.select { !it.hasCore }.random()

    // endregion
}