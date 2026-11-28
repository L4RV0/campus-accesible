/*
 * Modelo C4 de CampusAccesible, escrito como código (Structurizr DSL).
 *
 * Versión visual y editable: https://www.figma.com/board/j1YjFZgZReLn1tF3yNutlg
 * Este archivo es la fuente que comprueban las pruebas. También se abre con Structurizr Lite.
 *
 * Regla del equipo: cada «component» de este archivo es una clase real del paquete
 * indicado en su tecnología. ModeloSincronizadoTest falla si el modelo y el código
 * se separan (un componente sin clase o una clase sin componente).
 */
workspace "CampusAccesible" "Rutas e incidencias de accesibilidad en un campus ficticio" {

    !identifiers hierarchical

    model {
        visitante = person "Visitante" "Estudiante, docente o invitado que necesita moverse por el campus; puede tener movilidad reducida."
        gestor = person "Gestor de infraestructura" "Personal de mantenimiento que revisa y resuelve las incidencias reportadas."

        campus = softwareSystem "CampusAccesible" "Planifica recorridos que respetan las preferencias de accesibilidad y recibe reportes de barreras." {

            web = container "Página web" "Mapa esquemático y formulario de recorrido. (Pendiente: formulario para reportar barreras.)" "HTML, CSS y JavaScript (static/)" "Navegador"

            api = container "API REST" "Calcula recorridos y registra reportes de barreras. Guarda los datos en memoria." "Java 21, Spring Boot 3.5" {

                rutasController = component "RutasController" "Expone GET /api/mapa y GET /api/recorridos." "api"
                incidenciasController = component "IncidenciasController" "Expone POST /api/incidencias (reportar) y POST /api/incidencias/{id}/validacion y /rechazo (solo con X-Rol: GESTOR)." "api"
                manejadorErrores = component "ManejadorErrores" "Traduce errores a 400/404/422 con mensaje legible, sin traza (RNF-03)." "api"

                planificador = component "PlanificadorRutas" "Busca el recorrido más corto (Dijkstra) con los tramos disponibles en el momento de la consulta (cierres manuales y temporales) y explica por qué no hay ruta." "aplicacion"
                tramosController = component "TramosController" "Expone POST /api/tramos/{id}/cierres para programar cierres temporales (solo X-Rol: GESTOR)." "api"
                gestorCierres = component "GestorCierres" "Programa cierres temporales de un tramo con fecha de inicio y fin (HU-10)." "aplicacion"
                gestorIncidencias = component "GestorIncidencias" "Registra reportes y los valida o rechaza; validar cierra el tramo. (Pendiente HU-09: resolver.)" "aplicacion"

                mapaRepo = component "MapaCampusEnMemoria" "Lugares y tramos del campus ficticio (8 lugares, 11 tramos)." "infraestructura"
                incidenciaRepo = component "IncidenciaRepositoryEnMemoria" "Incidencias reportadas; se pierden al reiniciar." "infraestructura"
            }
        }

        visitante -> campus.web "Elige origen, destino y «sin escaleras»"
        visitante -> campus.api.incidenciasController "Reporta una barrera (hoy solo por la API; formulario web pendiente)" "JSON/HTTP"
        gestor -> campus.api.incidenciasController "Valida o rechaza incidencias (hoy solo por la API; pantalla del gestor pendiente)" "JSON/HTTP, X-Rol: GESTOR"
        gestor -> campus.api.tramosController "Programa cierres temporales (hoy solo por la API)" "JSON/HTTP, X-Rol: GESTOR"
        campus.web -> campus.api.rutasController "Pide mapa y recorridos" "JSON/HTTP"

        campus.api.rutasController -> campus.api.planificador "planificar(origen, destino, preferencias)" "IPlanificadorRutas"
        campus.api.rutasController -> campus.api.mapaRepo "Lee lugares y tramos" "IMapaRepository"
        campus.api.incidenciasController -> campus.api.gestorIncidencias "reportar, validar y rechazar" "IGestorIncidencias"
        campus.api.tramosController -> campus.api.gestorCierres "programar(tramoId, inicio, fin)" "IGestorCierres"
        campus.api.gestorCierres -> campus.api.mapaRepo "Agrega el cierre al tramo" "IMapaRepository"
        campus.api.planificador -> campus.api.mapaRepo "Lee tramos y su disponibilidad en la fecha de consulta" "IMapaRepository"
        campus.api.gestorIncidencias -> campus.api.mapaRepo "Comprueba el tramo y lo cierra al validar" "IMapaRepository"
        campus.api.gestorIncidencias -> campus.api.incidenciaRepo "Guarda y consulta incidencias" "IIncidenciaRepository"
    }

    views {
        systemContext campus "Nivel1_Contexto" "Quién usa CampusAccesible. No hay sistemas externos: el mapa es ficticio y vive dentro del sistema." {
            include *
            autoLayout lr
        }

        container campus "Nivel2_Contenedores" "La página web y la API. Un contenedor C4 no implica Docker." {
            include *
            autoLayout lr
        }

        component campus.api "Nivel3_Componentes" "Componentes de la API. Cada caja es una clase; cada flecha, una dependencia a través de una interfaz." {
            include *
            autoLayout lr
        }

        styles {
            element "Person" {
                shape person
                background #1f2a2e
                color #ffffff
            }
            element "Software System" {
                background #3d5a66
                color #ffffff
            }
            element "Container" {
                background #56636a
                color #ffffff
            }
            element "Navegador" {
                shape WebBrowser
            }
            element "Component" {
                background #eceff1
                color #1f2a2e
                stroke #3d5a66
            }
            relationship "Pendiente" {
                dashed true
                color #8a8a8a
            }
        }
    }
}
