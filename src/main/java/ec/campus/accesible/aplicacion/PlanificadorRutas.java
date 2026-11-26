package ec.campus.accesible.aplicacion;

import ec.campus.accesible.dominio.Preferencias;
import ec.campus.accesible.dominio.Recorrido;

public class PlanificadorRutas implements IPlanificadorRutas {

    private final IMapaRepository mapa;

    public PlanificadorRutas(IMapaRepository mapa) {
        this.mapa = mapa;
    }

    @Override
    public Recorrido planificar(String origenId, String destinoId, Preferencias preferencias) {
        throw new UnsupportedOperationException("Pendiente: HU-02");
    }
}
