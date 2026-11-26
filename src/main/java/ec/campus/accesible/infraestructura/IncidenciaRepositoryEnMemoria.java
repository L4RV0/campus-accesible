package ec.campus.accesible.infraestructura;

import ec.campus.accesible.aplicacion.IIncidenciaRepository;
import ec.campus.accesible.dominio.Incidencia;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Incidencias guardadas en memoria. Se pierden al reiniciar la aplicación:
 * es una limitación aceptada del prototipo.
 */
public class IncidenciaRepositoryEnMemoria implements IIncidenciaRepository {

    private final List<Incidencia> incidencias = new ArrayList<>();

    @Override
    public synchronized Incidencia guardar(Incidencia incidencia) {
        incidencias.removeIf(i -> i.id().equals(incidencia.id()));
        incidencias.add(incidencia);
        return incidencia;
    }

    @Override
    public synchronized Optional<Incidencia> buscar(String id) {
        return incidencias.stream().filter(i -> i.id().equals(id)).findFirst();
    }

    @Override
    public synchronized List<Incidencia> todas() {
        return List.copyOf(incidencias);
    }
}
