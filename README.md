Структура проекта:

* PlannerCore - планировщик (ныне разрабатывается Леной Артемьевой)
* json2aiml - Конвертер pddl/json -> AIML
* ChatAB - Пока базовая версия (program-ab-0.0.4.3). В дальнейшем, предлагаю Диме внести свои изменения именно сюда.


Бранч originalProgramAB - содержит ответвление оригинальной programAB.
Т.е. внесены минимальные изменения, обеспечивающие интегрируемость в IDE и налажен процесс сборки при помощи системы простройки maven.

В бранче master будет добавлена поддержка регулярных выражений.
В бранче logic будет проводится интеграция логики.
