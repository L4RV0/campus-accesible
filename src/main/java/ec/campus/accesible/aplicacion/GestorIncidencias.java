package ec.campus.accesible.aplicacion;

import ec.campus.accesible.dominio.Incidencia;
import ec.campus.accesible.dominio.TramoNoEncontradoException;

import java.time.Clock;
import java.time.LocalDateTime;

public class GestorIncidencias implements IGestorIncidencias {

    private final IMapaRepository mapa;
    private final IIncidenciaRepository incidencias;
    private final Clock reloj;

    public GestorIncidencias(IMapaRepository mapa, IIncidenciaRepository incidencias, Clock reloj) {
        this.mapa = mapa;
        this.incidencias = incidencias;
        this.reloj = reloj;
    }

    @Override
    public Incidencia reportar(String tramoId, String descripcion) {
        if (mapa.tramo(tramoId).isEmpty()) {
            throw new TramoNoEncontradoException(tramoId);
        }
        Incidencia incidencia = new Incidencia(siguienteId(), tramoId, descripcion, LocalDateTime.now(reloj));
        return incidencias.guardar(incidencia);
    }

    private String siguienteId() {
        return String.format("INC-%03d", incidencias.todas().size() + 1);
    }
}
