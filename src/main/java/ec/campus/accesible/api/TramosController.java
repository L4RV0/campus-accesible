package ec.campus.accesible.api;

import ec.campus.accesible.aplicacion.IGestorCierres;
import ec.campus.accesible.dominio.CierreTemporal;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/tramos")
public class TramosController {

    private final IGestorCierres gestorCierres;

    public TramosController(IGestorCierres gestorCierres) {
        this.gestorCierres = gestorCierres;
    }

    /** HU-10: solo el gestor programa cierres temporales (RNF-02). */
    @PostMapping("/{id}/cierres")
    @ResponseStatus(HttpStatus.CREATED)
    public CierreDto programarCierre(@PathVariable String id,
                                     @RequestHeader(value = "X-Rol", required = false) String rol,
                                     @RequestBody CierreDto cierre) {
        AccesoDenegadoException.exigirGestor(rol);
        CierreTemporal programado = gestorCierres.programar(id, cierre.inicio(), cierre.fin());
        return new CierreDto(id, programado.inicio(), programado.fin());
    }

    public record CierreDto(String tramoId, LocalDateTime inicio, LocalDateTime fin) {
    }
}
