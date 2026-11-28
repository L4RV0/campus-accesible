# ADR-0001: datos en memoria para el prototipo

Fecha: 26/11/2026. Estado: decisión existente documentada; pendiente de revisión del equipo. Referencia: PR #16, commit 1e132ea, que incorpora la base de HU-06 del PR #15.

## Contexto

El campus ficticio tiene ocho lugares y once tramos fijos. Se necesita demostrar cálculo de rutas y reporte de barreras sin instalar una base de datos. No se exige persistencia entre reinicios ni cuentas reales.

## Decisión

Conservar `MapaCampusEnMemoria` detrás de `IMapaRepository` y `IncidenciaRepositoryEnMemoria` detrás de `IIncidenciaRepository`. `GestorIncidencias` implementa `IGestorIncidencias`; el controlador usa ese puerto. `ConfiguracionComponentes` conecta las implementaciones e inyecta un `Clock` con zona America/Guayaquil.

En la versión revisada, el repositorio de incidencias usa `ArrayList<Incidencia>` y métodos sincronizados. `Incidencia` es una clase con `id: String`, `fechaReporte: LocalDateTime` y `fechaRevision: LocalDateTime`, inicialmente nula. Se crea en REPORTADA. Las operaciones de validar, rechazar y resolver siguen pendientes.

## Alternativas consideradas

- H2: permite consultas y persistencia configurable, pero requiere esquema y configuración adicionales.
- PostgreSQL: adecuado para almacenamiento duradero y varios procesos, con servicio, credenciales y migraciones.
- Archivo JSON: sencillo de inspeccionar, pero exige tratar escrituras concurrentes y recuperación de archivos incompletos.

## Consecuencias

El prototipo arranca sin servicios adicionales y permite pruebas aisladas. Los reportes se pierden al reiniciar, el consumo de memoria aumenta con los reportes y cada instancia mantiene datos independientes. Los puertos permiten sustituir el almacenamiento sin acoplar dominio y aplicación a Spring.

La sincronización de métodos individuales no hace atómica una operación compuesta. En la base original del PR #15 se detectó que dos reportes concurrentes podían recibir el mismo ID calculado con `todas().size() + 1` y uno sobrescribía al otro. Jeremy lo corrigió en `992cea8`: `GestorIncidencias` usa `AtomicLong.incrementAndGet()` y la prueba `reportesSimultaneos` comprueba IDs distintos y dos reportes almacenados. Esta solución corresponde al único servicio y repositorio en memoria de la aplicación; no define IDs distribuidos ni persistentes entre reinicios. El PR #16 en `1e132ea` aún contiene la base anterior y debe actualizarse al integrar el #15.

HU-07 y HU-09 deberán preservar consistencia entre estado de la incidencia y disponibilidad del tramo. Resolver una incidencia no debe reabrir un tramo que aún tenga otra incidencia validada; la convivencia con cierres temporales se definirá en HU-10.

## Verificación

`GestorIncidenciasTest.reporteValido` verifica fecha, estado y almacenamiento; `reporteNoCierraElTramo` protege la disponibilidad. `IncidenciasApiIntegracionTest.reporteSinValidarNoCambiaLaRuta` comprueba el recorrido por HTTP. `ArquitecturaTest` comprueba dependencias de capas.

La revisión de `1e132ea` ejecutó `mvnw verify`: 35 pruebas, 0 fallos y 0 errores. La revisión posterior de la corrección de HU-06 en `992cea8` ejecutó el mismo comando: 32 pruebas, 0 fallos y 0 errores, incluida `reportesSimultaneos`. Son ramas distintas; el conteo combinado deberá verificarse después de integrarlas.
