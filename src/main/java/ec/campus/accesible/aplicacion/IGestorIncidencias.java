package ec.campus.accesible.aplicacion;

import ec.campus.accesible.dominio.Incidencia;

public interface IGestorIncidencias {

    Incidencia reportar(String tramoId, String descripcion);
}
