package hexed

import arc.math.Mathf
import arc.struct.ObjectMap
import arc.struct.Seq
import arc.util.Timer
import buj.tl.Tl
import hexed.managers.Requests
import hexed.managers.Session
import hexed.structures.Member
import hexed.structures.Party
import hexed.utils.PartyUtils
import mindurka.ui.openMenu
import mindurka.util.K

const val pageSize = 6

class JoinMenu(val caller: Member) {
    val invites = ObjectMap<Party, JoinState>()
    private lateinit var parties: Seq<Party>

    fun open(startPage: Int = 1) {
        parties = PartyUtils.getAvailable(caller).copy()
        if (parties.isEmpty) {
            Tl.send(caller.player).done("{commands.no-players}")
            return
        }

        val maxPage = Mathf.ceil(parties.size.toFloat() / pageSize)
        var page = startPage.coerceIn(1, maxPage)

        caller.player.openMenu {
            if (parties.isEmpty) {
                Timer.schedule({closeDialog()}, 1f)
            }

            title("{commands.join.title}")

            val start = (page - 1) * pageSize
            val end = minOf(start + pageSize, parties.size)
            val currentParties = Seq<Party>()
            for (i in start until end) {
                currentParties.add(parties.get(i))
            }

            for (party in currentParties) {
                val state = invites.get(party, JoinState.NONE)

                val text = when (state) {
                    JoinState.NONE -> party.name
                    JoinState.SENT -> Tl.fmt(caller.player).done("{commands.join.invite-sent}")
                }

                optionText(text) {
                    when (state) {
                        JoinState.NONE -> {
                            Requests.sendInvite(caller, party)

                            invites.put(party, JoinState.SENT)
                            rerenderDialog()
                        }

                        JoinState.SENT -> {}
                    }
                }
            }

            group {
                optionText("") {
                    page = if (page > 1) page - 1 else maxPage
                    rerenderDialog()
                }
                optionText("[white]$page/$maxPage")
                optionText("") {
                    page = if (page < maxPage) page + 1 else 1
                    rerenderDialog()
                }
            }

            option("{generic.close}") { K.toString() }
        }
    }

    enum class JoinState {
        NONE, SENT
    }
}

class AcceptMenu(val caller: Member) {
    val incoming = ObjectMap<Member, AcceptState>()
    private lateinit var members: Seq<Member>

    fun open(startPage: Int = 1) {
        val party = Session.getParty(caller) ?: return
        members = Requests.getIncoming(party).copy()
        if (members.isEmpty) {
            Tl.send(caller.player).done("{commands.no-players}")
            return
        }

        val maxPage = Mathf.ceil(members.size.toFloat() / pageSize)
        var page = startPage.coerceIn(1, maxPage)

        caller.player.openMenu {
            title("{commands.accept.title}")

            val start = (page - 1) * pageSize
            val end = minOf(start + pageSize, members.size)
            val currentMembers = Seq<Member>()
            for (i in start until end) {
                currentMembers.add(members.get(i))
            }

            for (member in currentMembers) {
                group {
                    val state = incoming.get(member, AcceptState.NONE)
                    val text = when (state) {
                        AcceptState.NONE -> member.name
                        AcceptState.ACCEPT -> Tl.fmt(caller.player).done("{commands.accept.invite-success}")
                    }

                    optionText(text) {
                        when (state) {
                            AcceptState.NONE -> {
                                Requests.acceptInvite(party, member)
                                incoming.put(member, AcceptState.ACCEPT)
                                rerenderDialog()
                            }

                            AcceptState.ACCEPT -> {}
                        }
                    }
                }
            }

            group {
                optionText("") {
                    page = if (page > 1) page - 1 else maxPage
                    rerenderDialog()
                }
                optionText("[white]$page/$maxPage")
                optionText("") {
                    page = if (page < maxPage) page + 1 else 1
                    rerenderDialog()
                }
            }

            option("{generic.close}") { K.toString() }
        }
    }

    enum class AcceptState {
        NONE, ACCEPT
    }

}

