# CampusAccesible

Rutas e incidencias de accesibilidad dentro del campus. Proyecto 3 de la práctica integradora Kanban–XP de Modelado Ágil de Software (IS-505, período 2026-2), Grupo 12.

**Integrantes:** Ángel Álvarez, Erick Cevallos y Jeremy Delgado.

> ⚠️ Es un prototipo académico. El campus, los lugares y las distancias son **ficticios**. Los recorridos son orientativos y **no certifican la seguridad física** de ninguna ruta.

## Qué hace

- Muestra un mapa esquemático de lugares y tramos (plano, rampa o escalera).
- Calcula el recorrido más corto entre dos lugares.
- Con la opción «sin escaleras», nunca propone un tramo de escalera.
- No recomienda tramos cerrados.
- Si no hay un recorrido compatible, lo dice y explica el motivo.

En desarrollo: reporte y validación de incidencias (HU-06 a HU-09) y cierre temporal de una rampa (HU-10). El detalle está en [docs/backlog.md](docs/backlog.md).

## Cómo ejecutarlo

Solo necesitas **Java 21 o superior**. No hace falta instalar Maven: `mvnw` lo descarga la primera vez.

```powershell
.\mvnw.cmd test              # compila y ejecuta todas las pruebas
.\mvnw.cmd spring-boot:run   # levanta la API en http://localhost:8080
```

En Linux o macOS usa `./mvnw` en lugar de `.\mvnw.cmd`.

### Probar la API

Con la aplicación en marcha, desde otra terminal de PowerShell:

```powershell
# Mapa completo
Invoke-RestMethod http://localhost:8080/api/mapa

# De la Entrada principal al Bloque B, sin escaleras
Invoke-RestMethod "http://localhost:8080/api/recorridos?origen=ENT&destino=BLB&sinEscaleras=true"

# Al Auditorio solo se llega por escalera: responde 422 con el motivo
Invoke-RestMethod "http://localhost:8080/api/recorridos?origen=ENT&destino=AUD&sinEscaleras=true"
```

| Código | Lugar |
|--------|-------|
| ENT | Entrada principal |
| PAR | Parqueadero |
| BIB | Biblioteca |
| BLA | Bloque A |
| BLB | Bloque B |
| CAF | Cafetería |
| LAB | Laboratorios |
| AUD | Auditorio |

## Pruebas y cobertura

| Prueba | Qué comprueba |
|--------|---------------|
| `PlanificadorRutasTest` | Las tres reglas del enunciado (sin escaleras, tramo cerrado, sin ruta) y el camino más corto, cada una en un grafo pequeño escrito en la propia prueba. |
| `RutasApiIntegracionTest` | El flujo completo por HTTP con el mapa ficticio, incluidos los errores 400, 404 y 422. |
| `ArquitecturaTest` | Que `dominio` y `aplicacion` no dependan de Spring, de `infraestructura` ni de `api`, igual que en el diagrama de componentes. |

Después de `.\mvnw.cmd test`, el reporte de cobertura queda en `target/site/jacoco/index.html`. La cobertura dice qué líneas se ejecutaron durante las pruebas, no si las pruebas comprueban lo correcto. Por eso cada prueba lleva en su nombre la historia o regla que verifica.

La evidencia del ciclo rojo–verde–refactor está en el historial de commits (`test(...): rojo`, `feat(...): verde`, `refactor:`) y en las salidas guardadas en [docs/evidencias/tdd](docs/evidencias/tdd).

## Del modelo al código

| Nivel C4 | Dónde está |
|----------|------------|
| 1 · Contexto y 2 · Contenedores | `docs/arquitectura/workspace.dsl` (vistas `Nivel1_Contexto` y `Nivel2_Contenedores`) |
| 3 · Componentes | El mismo archivo, vista `Nivel3_Componentes`. Cada componente es una clase. |
| 4 · Código | [docs/arquitectura/nivel4-codigo.md](docs/arquitectura/nivel4-codigo.md), solo del planificador |

**Diagramas visuales y editables (FigJam):** https://www.figma.com/board/j1YjFZgZReLn1tF3yNutlg, con los niveles 1 a 4 y la matriz de trazabilidad.

`workspace.dsl` es la versión en código del mismo modelo, y es la que comprueban las pruebas.

La [matriz de trazabilidad](docs/matriz-trazabilidad.md) sigue 5 historias hasta su componente, su método y su prueba.

**¿Cómo sabemos que el diagrama no quedó desactualizado?** Dos pruebas lo comprueban en cada build:

| Prueba | Qué compara |
|--------|-------------|
| `ModeloSincronizadoTest` | Los componentes de `workspace.dsl` con las clases reales, en los dos sentidos |
| `MatrizTrazabilidadTest` | Que cada fila de la matriz apunte a un componente, un método y una prueba que existan |
| `ArquitecturaTest` | Las flechas del diagrama: `dominio` y `aplicacion` no dependen de Spring, `api` ni `infraestructura` |

## Estructura

```
docs/
  arquitectura/              modelo C4 como código y vista de código del planificador
  matriz-trazabilidad.md     historia → componente → código → prueba
  backlog.md                 visión, historias, RNF, prioridades, roadmap y cambio de requisito
  kanban.md                  columnas, políticas, WIP, DoD y registro de métricas
  evidencias/tdd/            salida de las pruebas en cada fase rojo/verde/refactor
src/main/java/ec/campus/accesible/
  dominio/                   Lugar, Tramo, Preferencias, Recorrido (sin dependencias externas)
  aplicacion/                PlanificadorRutas e interfaces (puertos)
  infraestructura/           MapaCampusEnMemoria (datos ficticios)
  api/                       RutasController y ManejadorErrores
  ConfiguracionComponentes   único lugar donde se conectan los componentes
.github/workflows/ci.yml     integración continua en cada push y pull request
```

## Forma de trabajo

- **Ramas:** `main` siempre en verde. Cada historia se trabaja en `feature/HU-xx-descripcion` y entra por pull request, con la revisión de otro integrante y el CI aprobado.
- **Commits:** prefijos `test:` (rojo), `feat:` (verde), `refactor:`, `docs:` y `ci:`, con el ID de la historia.
- **Tablero y políticas:** [docs/kanban.md](docs/kanban.md).
