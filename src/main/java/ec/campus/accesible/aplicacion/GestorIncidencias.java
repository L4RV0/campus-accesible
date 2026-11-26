package ec.campus.accesible.aplicacion;

import ec.campus.accesible.dominio.Incidencia;

import java.time.Clock;

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
        throw new UnsupportedOperationException("Pendiente: HU-06");
    }
}
