# Modelos de Ángel

Fecha: 26/11/2026. Alineación: PR #16 (`1e132ea`) y su base HU-06 del PR #15 (`8ea9565`). Estos modelos se entregan para revisión; no implican que los PR estén integrados en main.

## Seis entregables

| Entregable | Archivo |
|---|---|
| Glosario, interesados y reglas | `../glosario.md` |
| Diagrama de clases | `01-clases.puml` y `.png` |
| Diagrama de secuencia | `02-secuencia-recorrido.puml` y `.png` |
| Diagrama de estados | `03-estados-incidencia.puml` y `.png` |
| BPMN | `04-proceso-incidencias.bpmn`, `.drawio` y `.png` |
| ADR | `../adr/0001-datos-en-memoria.md` |

El C4 y la matriz son los entregables del equipo en PR #16: `docs/arquitectura/workspace.dsl`, `docs/arquitectura/nivel4-codigo.md` y `docs/matriz-trazabilidad.md`. No se sustituyen por las versiones anteriores del paquete de Ángel.

## Coincidencia de nombres

Los participantes se llaman **Visitante** y **Gestor de infraestructura**, igual que en el C4. Un participante BPMN representa a la persona; no se sustituye por el nombre de una clase Java.

| Actividad o concepto | Código / modelo correspondiente | Alcance |
|---|---|---|
| Registrar reporte | `IncidenciasController` → `IGestorIncidencias` / `GestorIncidencias.reportar` | API de HU-06 en PR #15 |
| Guardar incidencia | `IIncidenciaRepository` / `IncidenciaRepositoryEnMemoria` | En memoria |
| Tramo existente | `IMapaRepository` / `MapaCampusEnMemoria.tramo` | Validación del reporte |
| Pedir recorrido | `RutasController` → `IPlanificadorRutas` / `PlanificadorRutas.planificar` | Implementado |
| Revisar / validar / rechazar | Gestor de infraestructura; transiciones de `EstadoIncidencia` | HU-07 pendiente |
| Resolver / evaluar reapertura | Gestor de infraestructura; `RESUELTA` | HU-09 pendiente |

El BPMN es un proceso objetivo, no una interfaz ya disponible: HU-06 tiene API, pero el formulario web de reportes y la gestión posterior no están implementados en esta versión. Las etiquetas de negocio del BPMN se vinculan al código mediante esta tabla.

## Multiplicidades y estados

- Cada Tramo referencia dos lugares por ID. Un Lugar puede ser extremo de muchos tramos.
- Cada Recorrido calculado contiene uno o más lugares y cero o más tramos. Un mismo Tramo puede formar parte de muchos recorridos: asociación `Recorrido "0..*" -- "0..*" Tramo`, sin propiedad exclusiva.
- Con origen igual a destino hay un lugar y cero tramos. El planificador garantiza `lugares = tramos + 1`.
- Cada Incidencia se asocia a un tramo existente; un tramo puede tener muchos reportes.
- El enum ya contiene cuatro estados, pero solo la creación en REPORTADA está implementada. Las transiciones de gestión y el CierreTemporal son requisitos/propuestas pendientes.

## Regenerar

Los `.puml` se exportan con Java 21 y PlantUML: `java -jar plantuml.jar -charset UTF-8 -tpng docs/modelos/*.puml`. Usan Smetana y no requieren Graphviz. El `.bpmn` incluye BPMN DI y puede importarse en un modelador BPMN. El `.drawio` es una copia visual editable; el `.bpmn` es la fuente semántica del proceso.
