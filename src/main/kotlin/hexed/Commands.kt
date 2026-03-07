package hexed

import buj.tl.Tl
import hexed.managers.Session
import hexed.utils.PartyUtils
import mindurka.annotations.Command
import mindustry.gen.Player

@Command
private fun s(caller: Player) {
    val member = Session.getMember(caller) ?: return

    if (member.isDerelict) {
        Session.spawn(member)
        Tl.send(caller).done("{commands.s.game}")
        return
    }

    Session.getParty(member)?.let {
        if(member.isLeaderOf(it)) Session.destroy(it)
    }

    Session.leave(member)

    Tl.send(caller).done("{commands.s.spectator}")
}

@Command
private fun lb(caller: Player) {
    Renderer.showLeaderboard(caller)
}

@Command
private fun j(caller: Player) {
    val member = Session.getMember(caller) ?: return
    if(!PartyUtils.canJoin(member)) return

    JoinMenu(member).open()
}

@Command
private fun a(caller: Player) {
    val member = Session.getMember(caller) ?: return
    if(!PartyUtils.canAccept(member)) return

    AcceptMenu(member).open()
}
