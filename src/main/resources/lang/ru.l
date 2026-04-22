[generic.gameover]
title = [white]РАУНД ЗАВЕРШЕН
      =
      = {winner} [accent]победил, имея [accent]{controlled} захваченных хексов []
      =
      = [accent]Итоговые результаты:\n

[generic.rtv]
title = [white]РАУНД ПРОПУЩЕН\n\n

[game]
no-available-hex = [lightgray]Нет доступных хексов для появления.
player-destroyed = {defender}[accent] был уничтожен!
player-destroyed-by-player = {defender}[accent] был уничтожен игроком {attacker}[accent]!

player-dominating = {player}[white] Доминирует, захватив [accent]{1}[] хексов!

hex-lost = [lightgray]Хекс [accent]#{0}[] потерян!
hex-captured = [lightgray]Хекс [accent]#{0}[] захвачен!

[hex]
none = [lightgray]Не в хексе
progress = [lightgray]Хекс [accent]#{n}
         = [lightgray]Прогресс захвата: [accent]{progress}%

captured = [lightgray]Хекс [accent]#{n}
         = [accent][захвачен]

owner = [lightgray]Хекс [accent]#{n}
      = [accent][захвачен игроком: {player}[accent]]

[commands]
team = [scarlet]Вы уже в команде. Используйте /spectate
no-players = [scarlet]Нет доступных игроков.
player-not-found = [scarlet]Игрок не найден.
team-change-leader = [accent]Теперь {player}[accent] лидер команды.

[commands.lb]
description = Показать таблицу лидеров.
params = []

title = [accent]Таблица лидеров
      = {time} [white]осталось
      = {stats}

player = \n[accent]{pos}. [white]{name}[lightgray] [white]({controlled} хексов)

[commands.spectate]
description = Перейти в режим наблюдателя или обратно в игру.
params = []
game = Возвращение в игру.
spectator = Переключение в режим наблюдателя.

[commands.join]
description = Отправить запрос на вступление в команду.
params = []
title = Список команд
player-not-found = [scarlet]Игрок не найден.
sent = {player}[white] Хочет присоединиться в вашу команду.
     = Введите [accent]/accept
invite-sent = [green]Приглашение отправлено.

[commands.accept]
description = Принять игрока в команду.
params = []
title = Список приглашений
not-leader = [scarlet]Доступно только лидеру команды.
no-players = [scarlet]Нет доступных приглашений.
derelict = [scarlet]Не доступно в режиме наблюдателя.
success = {player}[white] Присоединился к команде.
invite-success = [green]Игрок принят.