package ec.campus.accesible.dominio;

/** Se intentó un cambio de estado que el ciclo de vida de la incidencia no permite. */
public class TransicionInvalidaException extends RuntimeException {

    public TransicionInvalidaException(String incidenciaId, EstadoIncidencia actual, EstadoIncidencia destino) {
        super("La incidencia " + incidenciaId + " está " + actual + " y no puede pasar a " + destino);
    }
}
