# Tablero Kanban de CampusAccesible

El tablero vive en GitHub Projects, enlazado a este repositorio. Este archivo guarda las reglas del tablero y el registro de fechas, para que queden versionados junto al código.

## Columnas

Usamos las seis columnas de la tarea de Kanban. Así también cumplimos la guía de la integradora: «Por hacer» equivale a «Listo», y «En revisión» y «Pruebas» son la columna «En revisión/pruebas» separada en dos.

| Columna | Entra cuando… | Sale cuando… |
|---------|---------------|--------------|
| **Backlog** | La historia tiene ID y redacción «Como… quiero… para…». | Tiene al menos dos criterios de aceptación y estimación acordada. |
| **Por hacer** | Cumple lo anterior y sus dependencias ya están en Hecho. | Una pareja la toma y hay espacio en En desarrollo. |
| **En desarrollo** (WIP 2) | Hay pareja asignada (conductor y observador) y rama `feature/` creada. | Las pruebas de sus criterios pasan en local y se abre el pull request. |
| **En revisión** | Hay un pull request abierto y el CI está en verde. | Otro integrante, que no programó la historia, aprobó el PR. |
| **Pruebas** (WIP 2) | El PR está aprobado. | Se comprobaron los criterios de aceptación contra la app levantada y se hizo merge a `main`. |
| **Hecho** | Cumple la definición de terminado. | — |

**Regla pull:** si En desarrollo o Pruebas ya tienen 2 tarjetas, nadie empieza otra. Primero se ayuda a sacar lo que está trabado.

## Definición de terminado (DoD)

Una tarjeta pasa a Hecho solo si:

1. Se cumplen todos sus criterios de aceptación.
2. Las pruebas automatizadas pasan en el CI.
3. Otra persona revisó el cambio (aprobación en el PR).
4. La documentación afectada está actualizada (README, backlog, matriz de trazabilidad o modelos).

## Cada tarjeta lleva

- ID de la historia (HU-xx) y título corto.
- Responsable o pareja.
- Criterio de aceptación principal.
- Enlace a la evidencia: commit, PR o ejecución del CI.
- Fechas de solicitud, inicio y fin, y los bloqueos (en la tabla de abajo).

## Registro de tarjetas y métricas

Medidas tomadas del historial de GitHub, no de memoria. Todas las horas son de Ecuador (UTC−5).

- **Solicitada:** 26/11 01:45, cuando las historias quedaron registradas en `docs/backlog.md` (commit `6624015`). El tablero se creó horas después, así que tomamos el backlog del repositorio como punto de partida.
- **Iniciada:** primer commit de la historia (en TDD, normalmente la prueba en rojo).
- **Terminada:** cierre del issue, que ocurre después de la revisión de otro integrante y del merge a `main`.
- **Lead time** = Terminada − Solicitada. **Cycle time** = Terminada − Iniciada.

| ID | Tarjeta | Iniciada | Terminada | Lead time | Cycle time | Revisó | Bloqueo / resolución |
|----|---------|----------|-----------|-----------|------------|--------|----------------------|
| HU-01 | Ver el mapa del campus | 26/11 01:50 | 26/11 15:25 | 13,7 h | 13,6 h | Erick (PR #14) | Esperó revisión |
| HU-02 | Pedir un recorrido | 26/11 01:46 | 26/11 19:11 | 17,4 h | 17,4 h | Ángel (issue #3) | Sin PR: estaba en `main` antes del tablero |
| HU-03 | Evitar escaleras | 26/11 01:47 | 26/11 16:58 | 15,2 h | 15,2 h | Ángel (PR #1) | Fallo controlado del CI, en rojo y luego en verde |
| HU-04 | No recomendar tramos cerrados | 26/11 01:47 | 26/11 19:12 | 17,4 h | 17,4 h | Ángel (issue #5) | Sin PR: estaba en `main` antes del tablero |
| HU-05 | Saber cuándo no hay ruta | 26/11 01:48 | 26/11 17:10 | 15,4 h | 15,4 h | Erick (issue #6) | Sin PR: estaba en `main` antes del tablero |
| HU-06 | Reportar una barrera | 26/11 17:15 | 26/11 20:26 | 18,7 h | 3,2 h | Ángel y Erick (PR #15) | Ángel pidió cambios (IDs duplicados con reportes simultáneos); corregido y aprobado |
| HU-07 | Validar o rechazar una incidencia | 27/11 23:17 | 28/11 00:48 | 47,1 h | 1,5 h | Erick (PR #18) | Sin Planning Poker ni pareja |
| HU-10 | Cierre temporal de una rampa | 27/11 23:21 | 28/11 01:07 | 47,4 h | 1,8 h | Erick (PR #19) | Erick pidió cambios (acceso concurrente a los cierres); corregido y aprobado |

Promedios de las 8 tarjetas: **lead time 24,0 h**, **cycle time 10,7 h**.

**Throughput** (tarjetas que llegaron a Hecho por día):

| Día | Terminadas | Cuáles |
|-----|-----------|--------|
| Jueves 26/11 | 6 | HU-01 a HU-06 |
| Viernes 27/11 | 0 | Sin avance de código hasta la noche |
| Sábado 28/11 (hasta la 01:07) | 2 | HU-07 y HU-10 |

El periodo dura menos de una semana, así que medimos el throughput por día y no por semana.

**Cómo leer los números.** El lead time de HU-07 y HU-10 es alto porque estuvieron en el backlog todo el viernes sin que nadie las empezara. Su cycle time es bajo porque, una vez empezadas, se programaron y revisaron la misma noche. No hay pruebas de que ese ritmo se pueda mantener: fueron historias pequeñas y hechas con asistencia de IA.

## Capturas

Tomar tres capturas del tablero en momentos distintos y guardarlas en `docs/evidencias/kanban/` con la fecha en el nombre (por ejemplo, `2026-11-26-tablero.png`).

## Rotación de parejas

| Historia | Cómo se hizo | Fecha |
|----------|--------------|-------|
| HU-01 a HU-05 | Jeremy con asistencia de IA; sin pareja | 26/11 |
| HU-06 | Jeremy con asistencia de IA; sin pareja. La corrección salió de la revisión de Ángel | 26/11 |
| HU-07 | Jeremy con asistencia de IA; sin pareja (el ping-pong planificado con Erick no se hizo) | 27/11 |
| HU-10 | Jeremy con asistencia de IA; sin pareja. La corrección salió de la revisión de Erick | 27–28/11 |

*(Completar si hubo trabajo en pareja real. Si no lo hubo, el informe debe decirlo así: la colaboración se dio en las revisiones de los PR, no programando en pareja.)*

## Cuello de botella y mejora

**Dónde se acumuló el trabajo.** El 26/11, HU-01 a HU-05 tenían el código terminado a la 01:50, pero llegaron a Hecho entre las 15:25 y las 19:12. Pasaron entre 13 y 17 horas en «En revisión», con 5 tarjetas a la vez en esa columna. La columna no tenía límite WIP, y los PR #1 y #14 se abrieron sin revisor asignado. HU-02, HU-04 y HU-05 ni siquiera tenían PR, porque se habían integrado antes de crear el tablero.

**Mejora aplicada.** Desde el PR #15, cada PR se abre con un revisor asignado y la tarjeta solo pasa a «En revisión» cuando hay PR y CI en verde.

**Antes y después.**

| | HU-01 a HU-05 (antes) | HU-06, HU-07 y HU-10 (después) |
|---|---|---|
| Cycle time promedio | 15,8 h | 2,2 h |
| Tarjetas a la vez en «En revisión» | hasta 5 | 1 o 2 |
| Cambios pedidos en revisión | 0 | 2 (IDs duplicados y concurrencia en cierres) |

**Límites del análisis.** Son pocas tarjetas y en días distintos. Parte de la mejora se debe a que las últimas historias se revisaron de noche, con los revisores conectados. Para la retrospectiva proponemos también poner un **límite WIP de 2 en «En revisión»**, igual que en «En desarrollo» y «Pruebas». *(Propuesta pendiente de acuerdo del equipo.)*
