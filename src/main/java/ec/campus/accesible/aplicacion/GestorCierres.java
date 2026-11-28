package ec.campus.accesible.aplicacion;

import ec.campus.accesible.dominio.CierreTemporal;
import ec.campus.accesible.dominio.TramoNoEncontradoException;

import java.time.LocalDateTime;

public class GestorCierres implements IGestorCierres {

    private final IMapaRepository mapa;

    public GestorCierres(IMapaRepository mapa) {
        this.mapa = mapa;
    }

    @Override
    public synchronized CierreTemporal programar(String tramoId, LocalDateTime inicio, LocalDateTime fin) {
        CierreTemporal cierre = new CierreTemporal(inicio, fin);
        mapa.tramo(tramoId).orElseThrow(() -> new TramoNoEncontradoException(tramoId)).agregarCierre(cierre);
        return cierre;
    }
}
