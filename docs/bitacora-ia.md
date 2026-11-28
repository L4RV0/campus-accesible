# Bitácora de trabajo asistido

Registro de decisiones y comprobaciones realizadas durante la preparación de la documentación de CampusAccesible.

| Fecha | Integrante | Objetivo | Herramienta | Fragmento relevante | Cómo se comprobó | Errores detectados | Decisión final |
|---|---|---|---|---|---|---|---|
| 2026-11-26 | Ángel | Revisar HU-02 | GitHub y pruebas Maven | Revisión del recorrido mínimo, origen igual a destino y errores de mapa | `main` con 21 pruebas en verde y ejecución de CI enlazada en el issue | La expectativa inicial de la ruta no coincidía con el mapa | Se dejó la evidencia en el issue y se cerró HU-02 |
| 2026-11-26 | Ángel | Revisar HU-04 | GitHub y pruebas Maven | Tramos cerrados, reapertura y ausencia de ruta compatible | `main` con 21 pruebas en verde y pruebas de cierre/reapertura | La revisión debía comprobar también el motivo de ausencia de ruta | Se documentó la revisión y se cerró HU-04 |
| 2026-11-26 | Ángel | Revisar PR #15 | Pruebas Maven y revisión de código | Dos hilos podían obtener el mismo ID con la lectura de tamaño más uno | Reproducción concurrente y después `./mvnw verify` con 32 pruebas | Carrera entre generación y guardado del identificador | Se aprobó la corrección con `AtomicLong` en `GestorIncidencias` |
| 2026-11-26 | Ángel | Revisar PR #16 | Revisión de documentación y Structurizr CLI | Se comprobaron nombres de clases, métodos, multiplicidades y alcance implementado | `./mvnw verify` con 35 pruebas y validación de `workspace.dsl` | La relación Recorrido–Tramo y el alcance del reporte web estaban sobredescritos | Se solicitaron dos correcciones concretas |
| 2026-11-26 | Ángel | Volver a revisar PR #16 | Revisión de código, Maven y Structurizr CLI | Multiplicidad `0..*` a `0..*`, funciones pendientes y rama actualizada con PR #15 | `./mvnw verify` con 36 pruebas, sin fallos ni errores; Structurizr válido | Las observaciones anteriores quedaron resueltas | Se aprobó el PR #16, que después fue fusionado |
| 2026-11-27 | Ángel | Preparar modelos UML, BPMN y ADR | Rama local basada en `main` | Sustitución de nombres obsoletos por las clases reales de `main` | Comparación directa con el código, regeneración de PNG y verificación de pruebas | La versión propia de HU-06 usaba record/UUID y repositorio distinto | Se conserva como trabajo local sin subir; los modelos se alinean con `main` |
| 2026-11-27 | Ángel | Registrar el uso de asistencia | Bitácora del proyecto | Se agotó el límite de tokens durante una sesión intermedia | Se anotó el riesgo sin inventar hora ni participantes | Dependencia operativa de una herramienta externa | Se deja como riesgo y se mantiene la revisión humana de nombres y resultados |

La implementación propia de HU-06 quedó descartada porque el equipo integró otra implementación en `main`. La decisión fue humana y evita duplicar funcionalidades.
