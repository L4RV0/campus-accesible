# Glosario, interesados y reglas del negocio

Fecha: 26/11/2026. Versión alineada con el PR #16, commit 1e132ea; pendiente de integración de los PR #15 y #16.
Ámbito: implementación de HU-06 y diseño de las historias siguientes. Los datos del campus son ficticios.

## Términos

| Término | Significado en este proyecto |
|---|---|
| Lugar | Punto identificado del mapa, como ENT o BIB. Clase `Lugar`. |
| Tramo | Conexión bidireccional entre dos lugares, con tipo, metros y disponibilidad. Clase `Tramo`. |
| Preferencias | Restricciones solicitadas al buscar una ruta. Actualmente solo `sinEscaleras`. |
| Recorrido | Lugares y tramos ordenados que llevan del origen al destino; incluye distancia total. |
| Barrera | Obstáculo observado que puede impedir el paso. Todavía no equivale a un cierre confirmado. |
| Incidencia | Reporte asociado a un tramo. HU-06 añade identificador, descripción, estado REPORTADA y fecha. |
| Validación | Decisión del gestor que confirma una barrera y cierra el tramo. Pendiente de HU-07. |
| Rechazo | Decisión del gestor que descarta el reporte sin modificar el tramo. Pendiente de HU-07. |
| Resolución | Cierre del seguimiento de una incidencia validada. Pendiente de HU-09. |
| Cierre temporal | Restricción programada con inicio y fin sobre una rampa. Diseño pendiente de HU-10. |
| Grafo | Representación de lugares como nodos y tramos como conexiones ponderadas por metros. |
| Ruta compatible | Recorrido que cumple preferencias y disponibilidad en todos sus tramos. |

## Interesados

| Interesado | Necesidad | Relación con el sistema |
|---|---|---|
| Visitante | Saber por dónde desplazarse y comunicar barreras | Consulta el mapa, pide rutas y reporta |
| Gestor de infraestructura | Revisar barreras y mantener la disponibilidad | Validación, rechazo, resolución y cierres previstos |
| Equipo de desarrollo | Mantener código, pruebas y modelos coherentes | Construye y comprueba el prototipo |
| Docente | Evaluar producto, proceso y evidencias | Revisa entregables y demostración |

## Reglas y alcance

1. Si se pide «sin escaleras», ningún tramo del recorrido puede ser ESCALERA.
2. Un tramo cerrado no se recomienda. Si no hay alternativa, se explica la ausencia de ruta.
3. Reportar no cierra ni reabre un tramo. Solo crea una incidencia REPORTADA con fecha del servidor.
4. El reporte exige descripción no vacía y un tramo existente. No se guardan reportes inválidos.
5. El gestor podrá validar o rechazar una incidencia REPORTADA (HU-07). La validación cerrará el tramo.
6. Solo se resolverá una incidencia VALIDADA (HU-09). No se reabrirá si queda otra validada pendiente.
7. Un cierre temporal vigente también impedirá usar la rampa. Aunque una incidencia se resuelva, no debe ignorarse ese cierre (regla de convivencia propuesta para HU-10).
8. Para HU-10 se propone intervalo [inicio, fin): el inicio está incluido y el fin excluido. El equipo debe acordar el caso inicio = fin; el backlog solo exige rechazar fin anterior al inicio.
9. El rol X-Rol: GESTOR es una simulación académica de autorización para las operaciones futuras del gestor; no son cuentas reales.
10. Se conservan los ocho lugares y once tramos. Los datos son ficticios y las rutas no certifican seguridad física.
11. El prototipo guarda datos en memoria; reiniciarlo elimina los reportes.

Las reglas 5 a 9 describen requisitos o propuestas pendientes; no son funcionalidades entregadas por HU-06.

## Nombres compartidos con el C4 y el código

| Concepto | Nombre exacto |
|---|---|
| Servicio que registra reportes | `GestorIncidencias`, puerto `IGestorIncidencias` |
| Almacenamiento de reportes | `IIncidenciaRepository`, adaptador `IncidenciaRepositoryEnMemoria` |
| Entrada HTTP de reportes | `IncidenciasController`, `POST /api/incidencias` |
| Planificación | `IPlanificadorRutas`, implementación `PlanificadorRutas` |
| Mapa | `IMapaRepository`, adaptador `MapaCampusEnMemoria` |
| Participantes BPMN / C4 | `Visitante` y `Gestor de infraestructura` |

`Gestor de infraestructura` es una persona; `GestorIncidencias` es un servicio de aplicación. No deben confundirse.
En la versión revisada, `Incidencia` es una clase, su `id` es `String`, y `fechaReporte` y `fechaRevision` son `LocalDateTime`. La fecha de revisión empieza en `null`; el reloj usa America/Guayaquil. El enum `EstadoIncidencia` ya declara REPORTADA, VALIDADA, RECHAZADA y RESUELTA, aunque solo la creación en REPORTADA está implementada.
