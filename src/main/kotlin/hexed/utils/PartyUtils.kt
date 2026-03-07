package hexed.utils

import arc.struct.Seq
import buj.tl.Tl
import hexed.managers.Requests
import hexed.managers.Session
import hexed.structures.Member
import hexed.structures.Party

object PartyUtils {
    fun getAvailable(from: Member): Seq<Party> {
        return Session.parties.values().toSeq()
            .select { party -> party.leader != from && !Requests.hasInvite(from, party) }
    }

    fun canJoin(member: Member): Boolean {
        val party = Session.getParty(member) ?: return true

        if (!party.isOne) {
            Tl.send(member.player).done("{commands.team}")
            return false
        }

        return true
    }

    fun canAccept(member: Member): Boolean {
        val party = Session.getParty(member)

        if (member.isDerelict) {
            Tl.send(member.player).done("{commands.a.derelict}")
            return false
        }

        if (!member.isLeaderOf(party)) {
            Tl.send(member.player).done("{commands.a.not-leader}")
            return false
        }

        return true
    }
}