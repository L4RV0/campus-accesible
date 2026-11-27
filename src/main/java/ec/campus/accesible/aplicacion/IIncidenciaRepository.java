package ec.campus.accesible.aplicacion;

import ec.campus.accesible.dominio.Incidencia;

import java.util.List;
import java.util.Optional;

/** Puerto hacia el almacenamiento de incidencias. */
public interface IIncidenciaRepository {

    Incidencia guardar(Incidencia incidencia);

    Optional<Incidencia> buscar(String id);

    List<Incidencia> todas();
}
