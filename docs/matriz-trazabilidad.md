# Matriz de trazabilidad

Cada fila sigue una historia desde su criterio de aceptación hasta la prueba que lo comprueba, pasando por el elemento del modelo C4 ([workspace.dsl](arquitectura/workspace.dsl)) y el método que lo implementa.

Versión visual en FigJam (último diagrama del tablero): https://www.figma.com/board/lePZWlkkVanQ41TG3kzr2b

`MatrizTrazabilidadTest` lee esta tabla en cada build: si se renombra o se borra una clase, un método o una prueba que aparece aquí, el CI falla y obliga a actualizar la matriz.

| Historia · criterio | Componente C4 | Implementación | Prueba | Resultado |
|---|---|---|---|---|
| HU-02 · Recibir el recorrido más corto entre origen y destino | `PlanificadorRutas` | `PlanificadorRutas.planificar` | `PlanificadorRutasTest.eligeElCaminoMasCorto` | Verde en CI · HU-02 revisada por Ángel (26/11) |
| HU-03 · Con «sin escaleras» ningún tramo del resultado es escalera | `PlanificadorRutas` | `Preferencias.admite` | `PlanificadorRutasTest.sinEscalerasExcluyeEscaleras` | Verde en CI · PR #1 aprobado por Ángel (26/11) |
| HU-04 · Un tramo cerrado no se recomienda | `PlanificadorRutas` | `Tramo.disponible` | `PlanificadorRutasTest.tramoCerradoNoSeRecomienda` | Verde en CI · HU-04 revisada por Ángel (26/11) |
| HU-05 · Sin ruta compatible se informa el motivo | `ManejadorErrores` | `PlanificadorRutas.planificar` | `RutasApiIntegracionTest.auditorioSinEscalerasNoTieneRuta` | Verde en CI · HU-05 revisada por Erick (26/11) |
| HU-06 · Un reporte válido queda «reportada» con su fecha | `GestorIncidencias` | `GestorIncidencias.reportar` | `GestorIncidenciasTest.reporteValido` | Verde en CI · PR #15 en revisión |

## Cómo leer una fila

Tomando HU-03 como ejemplo:

1. **Historia:** el visitante marca «sin escaleras» (HU-03, issue #4).
2. **Modelo:** el componente `PlanificadorRutas` del nivel 3 recibe las preferencias desde `RutasController`.
3. **Código:** `Preferencias.admite(tramo)` devuelve `false` para una escalera cuando la preferencia está activa, y la búsqueda descarta ese tramo.
4. **Prueba:** `sinEscalerasExcluyeEscaleras` arma un mapa donde la escalera es el camino más corto y comprueba que el resultado la evita.
5. **Evidencia:** los commits `test(HU-03): rojo` y `feat(HU-03): verde`, y la ejecución del CI en verde.

## Cuando cambia un requisito

Con el cambio «cierre temporal de una rampa» (HU-10), la fila de HU-04 tendrá que apuntar a la nueva regla de disponibilidad por fecha. Si el método cambia de nombre y nadie actualiza esta tabla, `MatrizTrazabilidadTest` lo detecta.
