package ec.campus.accesible.dominio;

/**
 * Ciclo de vida de una incidencia:
 * REPORTADA → VALIDADA → RESUELTA, o REPORTADA → RECHAZADA.
 */
public enum EstadoIncidencia {
    REPORTADA,
    VALIDADA,
    RECHAZADA,
    RESUELTA
}
