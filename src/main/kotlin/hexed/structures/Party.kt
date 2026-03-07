package hexed.structures

import arc.struct.Seq
import buj.tl.Tl
import hexed.managers.Hexes
import mindustry.game.Team

// Doesn't exist for derelict Team
class Party(var leader: Member) {
    val members: Seq<Member> = Seq()
    var active: Boolean = false;
    var kills: Int = 0
    var lastDamage: Party? = null;

    val name: String get() = leader.name

    val team: Team get() = leader.team

    val isOne: Boolean get() = members.size == 1

    val controlled: Int get() = Hexes.hexes.count { it?.owner == this  }

    init {
        members.add(leader)
    }

    fun leave(member: Member) {
        members.remove(member)

        if (member.isLeaderOf(this)) {
            leader = members.firstOrNull() ?: return
        } else {
            member.kill()
        }
    }

    fun accept(member: Member) {
        member.team(team)
        members.add(member)
        members.each {
            Tl.send(it.player)
                .put("player", member.name)
                .done("{commands.a.success}")
        }
    }

    fun destroy() {
        members.forEach { it.kill() }

        if (!active) return

        lastDamage?.kills++

        Tl.broadcast()
            .put("defender", name)
            .put("attacker", lastDamage?.name ?: "")
            .done(if (lastDamage == null) "{game.player-destroyed}" else "{game.player-destroyed-by-player}")
    }
}