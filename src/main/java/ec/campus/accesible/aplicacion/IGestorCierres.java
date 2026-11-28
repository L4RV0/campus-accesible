package ec.campus.accesible.aplicacion;

import ec.campus.accesible.dominio.CierreTemporal;

import java.time.LocalDateTime;

public interface IGestorCierres {

    /** HU-10: el gestor programa un cierre temporal de un tramo (obras, mantenimiento). */
    CierreTemporal programar(String tramoId, LocalDateTime inicio, LocalDateTime fin);
}
