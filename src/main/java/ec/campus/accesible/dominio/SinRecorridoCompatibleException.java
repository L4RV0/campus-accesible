package ec.campus.accesible.dominio;

import java.util.List;

public class SinRecorridoCompatibleException extends RuntimeException {

    private final List<MotivoSinRecorrido> motivos;

    public SinRecorridoCompatibleException(String mensaje, List<MotivoSinRecorrido> motivos) {
        super(mensaje);
        this.motivos = List.copyOf(motivos);
    }

    public List<MotivoSinRecorrido> motivos() {
        return motivos;
    }
}
