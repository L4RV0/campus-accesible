package ec.campus.accesible.dominio;

public class IncidenciaNoEncontradaException extends RuntimeException {

    public IncidenciaNoEncontradaException(String incidenciaId) {
        super("No existe la incidencia «" + incidenciaId + "»");
    }
}
