# Backlog de CampusAccesible

Grupo 12 · Modelado Ágil de Software (IS-505) · 2026-2

> Borrador inicial del 26/11/2026. Las estimaciones son una propuesta de partida: hay que confirmarlas en la sesión de Planning Poker y anotar ahí las diferencias y acuerdos (sección 5).

## 1. Visión

Moverse por el campus es fácil cuando uno conoce el camino y puede subir escaleras. Para una persona en silla de ruedas, con muletas, con un coche de bebé o cargando equipo, el mismo trayecto puede terminar en una escalera sin alternativa o en una rampa cerrada por obras de la que nadie avisó. Hoy esa información está en la cabeza de quien conoce el lugar, no en un sitio que se pueda consultar.

CampusAccesible propone un mapa esquemático del campus con los tramos entre lugares, marcados como plano, rampa o escalera. El visitante elige de dónde sale, a dónde va y si necesita evitar escaleras, y recibe un recorrido que respeta esa preferencia. Si no existe uno, la aplicación lo dice claramente en lugar de mandar a la persona por un camino que no puede usar. Cualquiera puede reportar una barrera (una rampa bloqueada, un tramo inundado), y el gestor de infraestructura la revisa: si la valida, el tramo deja de recomendarse hasta que se resuelva.

El alcance es un prototipo con ubicaciones ficticias y un grafo pequeño. No usa GPS ni certifica la seguridad física de ninguna ruta.

**Hipótesis a validar:** si un visitante con movilidad reducida puede ver antes de salir qué tramos tienen escalera o están cerrados, elegirá un recorrido que puede completar sin regresar. Lo mediremos con tareas observables en la validación con usuarios simulados.

## 2. Usuarios

- **Visitante:** estudiante, docente o invitado que necesita llegar a un lugar del campus. Puede tener o no restricciones de movilidad.
- **Gestor de infraestructura:** persona del área de mantenimiento que revisa los reportes, valida o rechaza incidencias y marca cuándo se resolvieron.

## 3. Historias de usuario

### HU-01 · Ver el mapa del campus
Como visitante quiero ver un mapa esquemático con los lugares y los tramos que los unen, para entender cómo está conectado el campus antes de moverme.
- Dado el mapa cargado, cuando lo abro, entonces veo cada lugar con su nombre y cada tramo con su tipo (plano, rampa o escalera).
- Dado un tramo cerrado, cuando veo el mapa, entonces ese tramo aparece marcado como cerrado y no solo con un color distinto (lleva texto o icono).

### HU-02 · Pedir un recorrido
Como visitante quiero elegir un origen y un destino, para recibir el recorrido más corto entre ambos.
- Dado un origen y un destino conectados, cuando pido el recorrido, entonces recibo la lista ordenada de lugares y la distancia total en metros.
- Dado que el origen y el destino son el mismo lugar, cuando pido el recorrido, entonces se me informa que ya estoy en el destino.
- Dado un lugar que no existe, cuando pido el recorrido, entonces recibo un mensaje de error claro y no una falla del sistema.

### HU-03 · Evitar escaleras
Como visitante con movilidad reducida quiero marcar la preferencia «sin escaleras», para que el recorrido no me lleve por tramos que no puedo usar.
- Dado que marqué «sin escaleras», cuando pido el recorrido, entonces ningún tramo del resultado es de tipo escalera, aunque el camino con escalera sea más corto.
- Dado que no marqué la preferencia, cuando pido el recorrido, entonces se puede usar una escalera si el camino es más corto.

### HU-04 · No recomendar tramos cerrados
Como visitante quiero que los tramos cerrados no aparezcan en mi recorrido, para no encontrarme con un paso bloqueado.
- Dado un tramo cerrado en el camino más corto, cuando pido el recorrido, entonces recibo uno alternativo que no lo usa.
- Dado que el tramo se vuelve a abrir, cuando pido el recorrido otra vez, entonces el tramo vuelve a poder usarse.

### HU-05 · Saber cuándo no hay ruta
Como visitante quiero que me digan claramente cuando no existe un recorrido compatible con mis preferencias, para buscar otra opción o pedir ayuda.
- Dado que el único acceso al destino es una escalera y marqué «sin escaleras», cuando pido el recorrido, entonces recibo el mensaje «No hay un recorrido sin escaleras disponible entre X e Y» y ningún recorrido parcial.
- Dado ese mismo caso, cuando veo la respuesta, entonces se indica el motivo (escaleras o tramos cerrados).

### HU-06 · Reportar una barrera
Como visitante quiero reportar una incidencia sobre un tramo (por ejemplo, una rampa bloqueada), para que el gestor la revise.
- Dado un tramo existente y una descripción, cuando envío el reporte, entonces se crea una incidencia en estado «reportada» con su fecha.
- Dado un reporte sin descripción o sobre un tramo inexistente, cuando lo envío, entonces se rechaza indicando qué dato falta.
- Dado un reporte recién creado, cuando pido un recorrido, entonces el tramo sigue disponible, porque aún no fue validado.

### HU-07 · Validar o rechazar una incidencia
Como gestor de infraestructura quiero validar o rechazar los reportes, para que solo las barreras confirmadas cambien los recorridos.
- Dado una incidencia reportada, cuando la valido, entonces pasa a «validada» y su tramo queda cerrado.
- Dado una incidencia reportada, cuando la rechazo, entonces pasa a «rechazada» y el tramo no cambia.
- Dado alguien sin rol de gestor, cuando intenta validar, entonces la operación se rechaza.

### HU-08 · Seguir el estado de un reporte
Como visitante o gestor quiero ver el estado y la fecha de revisión de cada incidencia, para saber si mi reporte ya se atendió.
- Dado una incidencia, cuando la consulto, entonces veo su estado, la fecha del reporte y la fecha de la última revisión.
- Dado una incidencia resuelta o rechazada, cuando intento validarla de nuevo, entonces se rechaza la transición.

### HU-09 · Marcar una incidencia como resuelta
Como gestor quiero marcar una incidencia validada como resuelta, para que el tramo vuelva a estar disponible.
- Dado una incidencia validada, cuando la marco como resuelta, entonces el tramo se reabre, salvo que tenga otra incidencia validada pendiente.
- Dado una incidencia que no está validada, cuando intento resolverla, entonces se rechaza la transición.

### HU-10 · Cierre temporal de una rampa (cambio de requisito)
Como gestor quiero cerrar una rampa durante un período con fecha de inicio y fin, para avisar de obras programadas sin tener que acordarme de reabrirla.
- Dado una rampa con cierre temporal vigente, cuando pido un recorrido dentro de ese período, entonces la rampa no se usa.
- Dado que el período terminó, cuando pido un recorrido, entonces la rampa vuelve a usarse sin intervención del gestor.
- Dado un cierre con fecha de fin anterior a la de inicio, cuando lo registro, entonces se rechaza.

### HU-11 · Ver el detalle del recorrido
Como visitante quiero ver cada tramo del recorrido con su tipo y longitud, más un aviso de que la ruta es orientativa, para decidir con información.
- Dado un recorrido calculado, cuando lo veo, entonces cada tramo muestra tipo y metros.
- Dado cualquier recorrido, cuando lo veo, entonces aparece el aviso «Prototipo con datos ficticios: no certifica la seguridad física de la ruta».

### HU-12 · Panel de incidencias abiertas
Como gestor quiero ver en un solo lugar las incidencias reportadas y validadas, ordenadas por antigüedad, para priorizar la revisión.
- Dado varias incidencias, cuando abro el panel, entonces veo primero las más antiguas sin revisar.
- Dado una incidencia resuelta, cuando abro el panel, entonces ya no aparece entre las abiertas.

## 4. Requisitos no funcionales

| ID | Requisito | Cómo se comprueba |
|----|-----------|-------------------|
| RNF-01 | **Accesibilidad básica.** La página se puede usar solo con teclado, los campos tienen etiqueta, el contraste cumple WCAG 2.1 AA y el estado de un tramo no se comunica solo con color. | Recorrido manual con teclado (Tab/Enter) y revisión con Lighthouse o axe; se adjuntan capturas. |
| RNF-02 | **Control de acceso.** Solo el gestor puede validar, rechazar o resolver incidencias y registrar cierres. En el prototipo el rol se simula con una cabecera `X-Rol: GESTOR`. | Prueba automatizada: sin el rol, la API responde 403. |
| RNF-03 | **Manejo de errores.** Ante datos inválidos o lugares inexistentes la API responde 400/404 con un mensaje legible, nunca 500 ni la traza interna. | Pruebas automatizadas de casos negativos sobre la API. |

## 5. Estimación y prioridad

Las estimaciones están en puntos de historia (Fibonacci). **Propuesta inicial, pendiente de Planning Poker.** Al estimar, anoten aquí qué valor dio cada uno en las historias donde no coincidieron y qué acordaron.

| ID | Historia | Criterios | Estimación | Prioridad | Dependencia | Incremento |
|----|----------|-----------|------------|-----------|-------------|------------|
| HU-01 | Ver el mapa del campus | 2 | 3 | Must | — | 1 |
| HU-02 | Pedir un recorrido | 3 | 5 | Must | HU-01 | 1 |
| HU-03 | Evitar escaleras | 2 | 2 | Must | HU-02 | 1 |
| HU-04 | No recomendar tramos cerrados | 2 | 2 | Must | HU-02 | 1 |
| HU-05 | Saber cuándo no hay ruta | 2 | 2 | Must | HU-03, HU-04 | 1 |
| HU-06 | Reportar una barrera | 3 | 3 | Must | HU-01 | 2 |
| HU-07 | Validar o rechazar una incidencia | 3 | 5 | Must | HU-06, HU-04 | 2 |
| HU-08 | Seguir el estado de un reporte | 2 | 2 | Should | HU-06 | 2 |
| HU-09 | Marcar una incidencia como resuelta | 2 | 3 | Should | HU-07 | 2 |
| HU-10 | Cierre temporal de una rampa | 3 | 5 | Should | HU-04 | 3 |
| HU-11 | Ver el detalle del recorrido | 2 | 2 | Could | HU-02 | 3 |
| HU-12 | Panel de incidencias abiertas | 2 | 3 | Could | HU-08 | 3 |

**Won't (fuera de este MVP):** navegación con GPS, cuentas de usuario reales, aplicación móvil nativa y fotos adjuntas en los reportes.

**Por qué este orden.** HU-02 a HU-05 llevan las tres reglas que el enunciado pide probar, y sin ellas no hay producto: van primero aunque sean pequeñas. HU-06 y HU-07 son la otra mitad de la demo (validar una incidencia y recalcular), así que también son Must. HU-10 entra en el tercer incremento porque llega como cambio de requisito y depende de que el cierre de tramos ya funcione. HU-11 y HU-12 mejoran la experiencia, pero la demo se sostiene sin ellas.

**MVP:** HU-01 a HU-07.

## 6. Roadmap

| Incremento | Historias | Resultado esperado | Evidencia de aceptación |
|------------|-----------|--------------------|-------------------------|
| 1 · Recorridos | HU-01 a HU-05 | La API calcula rutas respetando escaleras y tramos cerrados, y avisa cuando no hay ruta. | Pruebas de las tres reglas en verde en CI. |
| 2 · Incidencias | HU-06 a HU-09 | Un reporte validado cierra el tramo y el recorrido se recalcula. | Prueba de integración del flujo reportar → validar → recalcular. |
| 3 · Cambio y gestión | HU-10 a HU-12 | Cierres temporales con fechas y panel del gestor. | Pruebas del cierre vigente y vencido; demo grabada. |

## 7. Cambio de requisito: cierre temporal de una rampa

**Antes:** un tramo solo podía estar abierto o cerrado, y únicamente una incidencia validada lo cerraba. Para reabrirlo, el gestor tenía que resolver la incidencia a mano.

**Solicitud:** mantenimiento avisa que la rampa entre la Biblioteca y el Bloque B se cerrará por obras durante unos días y pide que el sistema la reabra solo al terminar el plazo.

**Después:** se agrega HU-10 en el incremento 3. Un tramo puede tener cierres temporales con fecha de inicio y fin, y la regla «un tramo cerrado no se recomienda» pasa a evaluarse según la fecha de la consulta. Afecta al modelo de dominio (nueva clase `CierreTemporal`), al planificador, a la matriz de trazabilidad y a un ADR. HU-11 y HU-12 bajan a Could para no mover la fecha de entrega.
