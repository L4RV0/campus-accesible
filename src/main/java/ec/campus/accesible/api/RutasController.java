package ec.campus.accesible.api;

import ec.campus.accesible.aplicacion.IMapaRepository;
import ec.campus.accesible.aplicacion.IPlanificadorRutas;
import ec.campus.accesible.dominio.Lugar;
import ec.campus.accesible.dominio.Preferencias;
import ec.campus.accesible.dominio.Recorrido;
import ec.campus.accesible.dominio.Tramo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RutasController {

    static final String AVISO =
            "Prototipo con datos ficticios: no certifica la seguridad física de la ruta.";

    private final IMapaRepository mapa;
    private final IPlanificadorRutas planificador;

    public RutasController(IMapaRepository mapa, IPlanificadorRutas planificador) {
        this.mapa = mapa;
        this.planificador = planificador;
    }

    /** HU-01: lugares y tramos con su tipo y estado. */
    @GetMapping("/mapa")
    public MapaDto mapa() {
        return new MapaDto(mapa.lugares(), mapa.tramos().stream().map(TramoDto::de).toList());
    }

    /** HU-02 a HU-05: recorrido más corto que respeta preferencias y cierres. */
    @GetMapping("/recorridos")
    public RecorridoDto recorrido(@RequestParam String origen,
                                  @RequestParam String destino,
                                  @RequestParam(defaultValue = "false") boolean sinEscaleras) {
        Recorrido recorrido = planificador.planificar(origen, destino, new Preferencias(sinEscaleras));
        return new RecorridoDto(
                recorrido.lugares(),
                recorrido.tramos().stream().map(TramoDto::de).toList(),
                recorrido.metrosTotales(),
                AVISO);
    }

    public record MapaDto(List<Lugar> lugares, List<TramoDto> tramos) {
    }

    public record RecorridoDto(List<Lugar> lugares, List<TramoDto> tramos, int metrosTotales, String aviso) {
    }

    public record TramoDto(String id, String desde, String hasta, String tipo, int metros, boolean cerrado) {
        static TramoDto de(Tramo t) {
            return new TramoDto(t.id(), t.extremoA(), t.extremoB(), t.tipo().name(), t.metros(), !t.disponible());
        }
    }
}
