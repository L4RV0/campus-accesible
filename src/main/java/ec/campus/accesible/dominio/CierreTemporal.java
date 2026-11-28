package ec.campus.accesible.dominio;

import java.time.LocalDateTime;

/**
 * HU-10 (cambio de requisito): un tramo no se recomienda entre {@code inicio} (incluido)
 * y {@code fin} (excluido). Al llegar el fin, el tramo vuelve solo, sin que el gestor lo reabra.
 */
public record CierreTemporal(LocalDateTime inicio, LocalDateTime fin) {

    public CierreTemporal {
        if (inicio == null || fin == null || !fin.isAfter(inicio)) {
            throw new IllegalArgumentException("La fecha de fin del cierre debe ser posterior a la de inicio");
        }
    }

    public boolean vigenteEn(LocalDateTime momento) {
        return !momento.isBefore(inicio) && momento.isBefore(fin);
    }
}
