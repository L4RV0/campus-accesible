package ec.campus.accesible.dominio;

public class LugarNoEncontradoException extends RuntimeException {

    public LugarNoEncontradoException(String lugarId) {
        super("No existe el lugar «" + lugarId + "» en el mapa");
    }
}
