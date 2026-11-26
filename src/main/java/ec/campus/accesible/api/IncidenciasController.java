package ec.campus.accesible.api;

import ec.campus.accesible.aplicacion.IGestorIncidencias;
import ec.campus.accesible.dominio.Incidencia;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/incidencias")
public class IncidenciasController {

    private final IGestorIncidencias gestor;

    public IncidenciasController(IGestorIncidencias gestor) {
        this.gestor = gestor;
    }

    /** HU-06: cualquier visitante puede reportar una barrera sobre un tramo. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IncidenciaDto reportar(@RequestBody ReporteDto reporte) {
        return IncidenciaDto.de(gestor.reportar(reporte.tramoId(), reporte.descripcion()));
    }

    public record ReporteDto(String tramoId, String descripcion) {
    }

    public record IncidenciaDto(String id, String tramoId, String descripcion, String estado,
                                LocalDateTime fechaReporte, LocalDateTime fechaRevision) {
        static IncidenciaDto de(Incidencia i) {
            return new IncidenciaDto(i.id(), i.tramoId(), i.descripcion(), i.estado().name(),
                    i.fechaReporte(), i.fechaRevision());
        }
    }
}
