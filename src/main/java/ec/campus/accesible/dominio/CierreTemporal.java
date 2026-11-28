package ec.campus.accesible.dominio;

import java.time.LocalDateTime;

/** HU-10: un tramo no se recomienda entre inicio (incluido) y fin (excluido). */
public record CierreTemporal(LocalDateTime inicio, LocalDateTime fin) {

    public boolean vigenteEn(LocalDateTime momento) {
        return false;
    }
}
