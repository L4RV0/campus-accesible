package ec.campus.accesible.aplicacion;

import ec.campus.accesible.dominio.Preferencias;
import ec.campus.accesible.dominio.Recorrido;

public interface IPlanificadorRutas {

    Recorrido planificar(String origenId, String destinoId, Preferencias preferencias);
}
