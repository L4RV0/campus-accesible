package ec.campus.accesible.dominio;

import java.time.LocalDateTime;

/** Una barrera reportada sobre un tramo (rampa bloqueada, tramo inundado...). */
public class Incidencia {

    private final String id;
    private final String tramoId;
    private final String descripcion;
    private final LocalDateTime fechaReporte;
    private EstadoIncidencia estado;
    private LocalDateTime fechaRevision;

    public Incidencia(String id, String tramoId, String descripcion, LocalDateTime fechaReporte) {
        this.id = id;
        this.tramoId = tramoId;
        this.descripcion = descripcion;
        this.fechaReporte = fechaReporte;
        this.estado = EstadoIncidencia.REPORTADA;
    }

    public String id() { return id; }
    public String tramoId() { return tramoId; }
    public String descripcion() { return descripcion; }
    public LocalDateTime fechaReporte() { return fechaReporte; }
    public EstadoIncidencia estado() { return estado; }
    public LocalDateTime fechaRevision() { return fechaRevision; }
}
