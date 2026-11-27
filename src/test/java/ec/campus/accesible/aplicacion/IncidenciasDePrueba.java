package ec.campus.accesible.aplicacion;

import ec.campus.accesible.dominio.Incidencia;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class IncidenciasDePrueba implements IIncidenciaRepository {

    private final List<Incidencia> guardadas = new ArrayList<>();

    @Override
    public synchronized Incidencia guardar(Incidencia incidencia) {
        guardadas.removeIf(i -> i.id().equals(incidencia.id()));
        guardadas.add(incidencia);
        return incidencia;
    }

    @Override
    public synchronized Optional<Incidencia> buscar(String id) {
        return guardadas.stream().filter(i -> i.id().equals(id)).findFirst();
    }

    @Override
    public synchronized List<Incidencia> todas() {
        return List.copyOf(guardadas);
    }
}
