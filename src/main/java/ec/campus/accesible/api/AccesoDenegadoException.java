package ec.campus.accesible.api;

/** RNF-02: la operación es solo para el gestor de infraestructura. */
public class AccesoDenegadoException extends RuntimeException {

    public AccesoDenegadoException() {
        super("Solo el gestor de infraestructura puede hacer esta operación");
    }

    /**
     * En el prototipo el rol se simula con la cabecera {@code X-Rol}. En producción vendría
     * de un inicio de sesión real; este método es el único lugar que habría que cambiar.
     */
    static void exigirGestor(String rol) {
        if (!"GESTOR".equalsIgnoreCase(rol == null ? "" : rol.trim())) {
            throw new AccesoDenegadoException();
        }
    }
}
