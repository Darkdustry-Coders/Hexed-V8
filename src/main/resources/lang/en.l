[generic.gameover]
title = [white]ROUND OVER
      =
      = {winner} is [accent]victorious[] with [accent]{controlled} hexes[] captured
      =
      = [accent]Final scores:\n

[generic.rtv]
title = [white]ROUND SKIPPED\n\n

[game]
no-available-hex = No available hex found to spawn.
                 = You will be [accent]spectating[] the game.
player-destroyed = {defender}[accent] has been eliminated!
player-destroyed-by-player = {defender}[accent] has been eliminated by {attacker}[accent]!

player-dominating = {player}[white] is dominating the game with [accent]{1}[] hexes captured!

hex-lost = [lightgray]Hex [accent]#{0}[] lost!
hex-captured = [lightgray]Hex [accent]#{0}[] captured!

[hex]
none = [lightgray]Not in hex
progress = [lightgray]Hex [accent]#{n}
         = [lightgray]Capture progress: [accent]{progress}%

captured = [lightgray]Hex [accent]#{n}
         = [accent][Captured]

owner = [lightgray]Hex [accent]#{n}
      = [accent][Captured by {player}[accent]]

[commands]
team = [scarlet]You are already on a team. Use /s first.
no-players = [scarlet]No available players.
player-not-found = [scarlet]Player not found.
team-change-leader = [accent]Now {player}[accent] is the team leader.

[commands.lb]
description = Show the current leaderboard
params = []

title = [accent]Leaderboard
      = {time} [white]remaining
      = {stats}

player = \n[accent]{pos}. [white]{name}[lightgray] [white]({controlled} hexes)

[commands.s]
description = Switch to spectator mode or return to the game.
params = []
game = Returning to the game.
spectator = Switching to spectator mode.

[commands.j]
description = Invite to join a team.
params = []
title = Available players
sent = {player}[white] wants to join your team.
     = Enter [accent]/a
invite-sent = [green]Invitation sent

[commands.a]
description = Accept a player into the team.
params = []
title = Team invites
not-leader = [scarlet]Only the team leader can do this.
no-players = [scarlet]No available invites.
derelict = [scarlet]Not available in spectator mode.
success = {player}[white] has joined the team.
invite-success = [green] Accepted invite