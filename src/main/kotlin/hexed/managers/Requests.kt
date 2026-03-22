package hexed.managers

import arc.struct.ObjectMap
import arc.struct.Seq
import buj.tl.Tl
import hexed.structures.Member
import hexed.structures.Party
import mindurka.util.getOrPut

object Requests : Manager {

    private val outcoming = ObjectMap<Member, Seq<Party>>()
    private val incoming = ObjectMap<Party, Seq<Member>>()

    override fun play() {}
    override fun reset() {
        outcoming.clear()
        incoming.clear()
    }

    fun sendInvite(sender: Member, target: Party) {
        if(hasInvite(sender,target)) {
            return
        }

        outcoming.getOrPut(sender) { Seq() }
        outcoming[sender].add(target)

        incoming.getOrPut(target) { Seq() }
        incoming[target].add(sender)

        Tl.send(target.leader.player)
            .put("player", sender.name)
            .done("{commands.j.sent}")

    }

    fun acceptInvite(acceptor: Party, target: Member) {
        outcoming.remove(target)
        outcoming.remove(acceptor.leader)

        incoming.values().toSeq().each { it.remove(target) }
        incoming.values().toSeq().each { it.remove(acceptor.leader) }

        Session.leave(target)
        acceptor.accept(target)
    }

    fun hasInvite(sender: Member, target: Party) = outcoming[sender]?.contains(target) == true

    fun getIncoming(target: Party) = incoming[target] ?: Seq()
}
