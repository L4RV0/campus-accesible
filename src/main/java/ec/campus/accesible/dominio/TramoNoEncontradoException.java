package ec.campus.accesible.dominio;

public class TramoNoEncontradoException extends RuntimeException {

    public TramoNoEncontradoException(String tramoId) {
        super("No existe el tramo «" + tramoId + "» en el mapa");
    }
}
