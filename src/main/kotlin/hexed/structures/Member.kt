package hexed.structures

import mindustry.game.Team
import mindustry.gen.Call
import mindustry.gen.Player

// Ensures the player is up to date
class Member(var player: Player) {
    val isDerelict: Boolean
        get() = team == Team.derelict

    val name: String
        get() = player.name

    val team: Team
        get() = player.team()

    fun team(team: Team) = player.team(team)

    fun kill() {
        team(Team.derelict)
        player.clearUnit()
        Call.hideHudText()
    }

    fun reconnect(player: Player) {
        player.team(team)
        this.player = player
    }

    fun isLeaderOf(party: Party?): Boolean = this == party?.leader
}

