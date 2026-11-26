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

## Registro de tarjetas

> Anoten las fechas **reales** cuando muevan cada tarjeta. Si algún dato se reconstruye o se simula, márquenlo con *(simulado)* para que el informe lo distinga.

- **Solicitada:** día en que la tarjeta entra a Por hacer.
- **Iniciada:** día en que entra a En desarrollo.
- **Terminada:** día en que llega a Hecho.
- **Lead time** = Terminada − Solicitada. **Cycle time** = Terminada − Iniciada.

| ID | Tarjeta | Responsable | Solicitada | Iniciada | Terminada | Lead time | Cycle time | Bloqueo / resolución |
|----|---------|-------------|------------|----------|-----------|-----------|------------|----------------------|
| HU-02 | Pedir un recorrido | | | | | | | |
| HU-03 | Evitar escaleras | | | | | | | |
| HU-04 | No recomendar tramos cerrados | | | | | | | |
| HU-05 | Saber cuándo no hay ruta | | | | | | | |
| HU-01 | Ver el mapa del campus | | | | | | | |
| HU-06 | Reportar una barrera | | | | | | | |
| HU-07 | Validar o rechazar una incidencia | | | | | | | |

**Throughput:** tarjetas terminadas por semana. Se cuenta al cierre de cada semana.

## Capturas

Tomar tres capturas del tablero en momentos distintos y guardarlas en `docs/evidencias/kanban/` con la fecha en el nombre (por ejemplo, `2026-11-26-tablero.png`).

## Rotación de parejas

| Historia | Conductor | Observador | Fecha |
|----------|-----------|------------|-------|
| | | | |

Con tres integrantes, la rotación natural es Ángel–Erick, Erick–Jeremy y Jeremy–Ángel. El tercero de cada turno hace la revisión del PR.

## Cuello de botella y mejora

*(Completar con lo que observen: en qué columna se acumularon tarjetas, qué acordaron cambiar y cómo quedó el flujo antes y después.)*
