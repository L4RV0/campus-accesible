package ec.campus.accesible.dominio;

/** Lo que el visitante pide al planificar. Por ahora solo «sin escaleras». */
public record Preferencias(boolean sinEscaleras) {

    public static Preferencias ninguna() {
        return new Preferencias(false);
    }

    public static Preferencias evitarEscaleras() {
        return new Preferencias(true);
    }

    /** Regla 1: con «sin escaleras» activo, ningún tramo de escalera es aceptable. */
    public boolean admite(Tramo tramo) {
        return !(sinEscaleras && tramo.tipo() == TipoTramo.ESCALERA);
    }
}
