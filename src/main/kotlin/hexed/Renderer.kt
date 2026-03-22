package hexed

import arc.struct.Seq
import arc.util.Align
import buj.tl.Tl
import hexed.managers.Game
import hexed.managers.Hexes
import hexed.managers.Session
import hexed.structures.Member
import hexed.structures.Party
import mindustry.core.UI
import mindustry.gen.Call
import mindustry.gen.Groups
import mindustry.gen.Player

object Renderer {
    // region show
    fun showHudText() {
        Session.parties.values().toSeq().each { party ->
            party.members.forEach { member -> Call.setHudText(member.player.con, getHud(member)) }
        }
    }

    fun showLeaderboard() {
        Groups.player.forEach { showLeaderboard(it) }
    }

    fun showLeaderboard(player: Player) {
        val leaderboard = Session.getLeaderboard(4)

        Call.infoPopup(
            player.con,
            getLeaderboardMessage(player, leaderboard),
            12f,
            Align.left, 0, 0, 50, 0
        )
    }

    fun showEndGameMessage() {
        val leaderboard = Session.getLeaderboard(8)

        Groups.player.forEach {
            Call.infoMessage(
                it.con,
                getEndMessage(it, leaderboard, "{generic.gameover.title}")
            )
        }
    }

    fun showRtvMessage() {
        val leaderboard = Session.getLeaderboard(8)

        Groups.player.forEach {
            Call.infoMessage(
                it.con,
                getEndMessage(it, leaderboard, "{generic.rtv.title}")
            )
        }
    }

    // endregion
    // region get

    fun getHud(member: Member): String {
        val hex = Hexes.getHex(member.player)
        val party = Session.getParty(member)

        if (hex == null) return Tl.fmt(member.player).done("{hex.none}")

        return if (hex.owner == null) {
            Tl.fmt(member.player)
                .put("n", hex.id.toString())
                .put("progress", hex.getProgressPercent(member.team).toString())
                .done("{hex.progress}")
        } else if (hex.owner == party) {
            Tl.fmt(member.player)
                .put("n", hex.id.toString())
                .done("{hex.captured}")
        } else {
            Tl.fmt(member.player)
                .put("n", hex.id.toString())
                .put("player", hex.owner!!.name)
                .done("{hex.owner}")
        }
    }

    fun getLeaderboardMessage(player: Player, leaderboard: Seq<Party>): String {
        return Tl.fmt(player)
            .put("time", UI.formatTime(Game.counter))
            .put("stats", getTeamStats(player, leaderboard))
            .done("{commands.lb.title}")
    }

    private fun getEndMessage(player: Player, leaderboard: Seq<Party>, titleKey: String): String {
        if (leaderboard.isEmpty) return ""

        val winner = leaderboard.first()
        return Tl.fmt(player)
            .put("winner", winner.name)
            .put("controlled", winner.controlled.toString())
            .done(titleKey) + getTeamStats(player, leaderboard)
    }

    fun getTeamStats(player: Player, leaderboard: Seq<Party>): String {
        val builder = StringBuilder()

        for (i in 0 until leaderboard.size) {
            val team = leaderboard[i]

            builder.append(
                Tl.fmt(player)
                    .put("pos", (i + 1).toString())
                    .put("name", team.name)
                    .put("controlled", team.controlled.toString())
                    .done("{commands.lb.player}")
            )
        }

        return builder.toString()
    }

    // endregion
}