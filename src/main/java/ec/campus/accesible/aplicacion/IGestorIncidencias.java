package ec.campus.accesible.aplicacion;

import ec.campus.accesible.dominio.Incidencia;

public interface IGestorIncidencias {

    Incidencia reportar(String tramoId, String descripcion);

    /** HU-07: el gestor confirma la barrera; el tramo deja de recomendarse. */
    Incidencia validar(String incidenciaId);

    /** HU-07: el gestor descarta el reporte; el tramo no cambia. */
    Incidencia rechazar(String incidenciaId);
}
