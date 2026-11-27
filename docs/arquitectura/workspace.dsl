/*
 * Modelo C4 de CampusAccesible, escrito como código (Structurizr DSL).
 *
 * Cómo verlo: copia este archivo en https://structurizr.com/dsl y pulsa «Render»,
 * o ábrelo con Structurizr Lite.
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

        campus = softwareSystem "CampusAccesible" "Planifica recorridos que respetan las preferencias de accesibilidad y gestiona reportes de barreras." {

            web = container "Página web" "Mapa esquemático, formulario de recorrido y reporte de barreras." "HTML, CSS y JavaScript (static/)" "Navegador"

            api = container "API REST" "Calcula recorridos y gestiona incidencias. Guarda los datos en memoria." "Java 21, Spring Boot 3.5" {

                rutasController = component "RutasController" "Expone GET /api/mapa y GET /api/recorridos." "api"
                incidenciasController = component "IncidenciasController" "Expone POST /api/incidencias." "api"
                manejadorErrores = component "ManejadorErrores" "Traduce errores a 400/404/422 con mensaje legible, sin traza (RNF-03)." "api"

                planificador = component "PlanificadorRutas" "Busca el recorrido más corto (Dijkstra) con los tramos que el visitante puede usar y explica por qué no hay ruta." "aplicacion"
                gestorIncidencias = component "GestorIncidencias" "Registra reportes de barreras y controla su ciclo de vida." "aplicacion"

                mapaRepo = component "MapaCampusEnMemoria" "Lugares y tramos del campus ficticio (8 lugares, 11 tramos)." "infraestructura"
                incidenciaRepo = component "IncidenciaRepositoryEnMemoria" "Incidencias reportadas; se pierden al reiniciar." "infraestructura"
            }
        }

        visitante -> campus.web "Elige origen, destino y «sin escaleras»; reporta barreras"
        gestor -> campus.web "Revisa y valida incidencias"
        campus.web -> campus.api.rutasController "Pide mapa y recorridos" "JSON/HTTP"
        campus.web -> campus.api.incidenciasController "Envía reportes" "JSON/HTTP"

        campus.api.rutasController -> campus.api.planificador "planificar(origen, destino, preferencias)" "IPlanificadorRutas"
        campus.api.rutasController -> campus.api.mapaRepo "Lee lugares y tramos" "IMapaRepository"
        campus.api.incidenciasController -> campus.api.gestorIncidencias "reportar(tramoId, descripción)" "IGestorIncidencias"
        campus.api.planificador -> campus.api.mapaRepo "Lee tramos y su estado" "IMapaRepository"
        campus.api.gestorIncidencias -> campus.api.mapaRepo "Comprueba que el tramo exista" "IMapaRepository"
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
        }
    }
}
