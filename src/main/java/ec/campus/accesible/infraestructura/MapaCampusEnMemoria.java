package ec.campus.accesible.infraestructura;

import ec.campus.accesible.aplicacion.IMapaRepository;
import ec.campus.accesible.dominio.Lugar;
import ec.campus.accesible.dominio.Tramo;

import java.util.List;
import java.util.Optional;

import static ec.campus.accesible.dominio.TipoTramo.ESCALERA;
import static ec.campus.accesible.dominio.TipoTramo.PLANO;
import static ec.campus.accesible.dominio.TipoTramo.RAMPA;

/**
 * Mapa de un campus FICTICIO. Los nombres y distancias son inventados para el prototipo
 * y no describen ningún campus real.
 *
 * <pre>
 *  Parqueadero ─(rampa 150)─ Biblioteca ─(rampa 90)─ Bloque B ─(rampa 110)─ Laboratorios ─(escalera 30)─ Auditorio
 *      │                        │    \                  │                       │
 *  (plano 80)              (plano 120) (rampa 70)  (escalera 40)            (plano 100)
 *      │                        │       \               │                       │
 *  Entrada principal ───────────┘        Bloque A ──────┘── (plano 50) ── Cafetería
 *      └──────────────(escalera 60)──────────┘
 * </pre>
 */
public class MapaCampusEnMemoria implements IMapaRepository {

    private final List<Lugar> lugares = List.of(
            new Lugar("ENT", "Entrada principal"),
            new Lugar("PAR", "Parqueadero"),
            new Lugar("BIB", "Biblioteca"),
            new Lugar("BLA", "Bloque A"),
            new Lugar("BLB", "Bloque B"),
            new Lugar("CAF", "Cafetería"),
            new Lugar("LAB", "Laboratorios"),
            new Lugar("AUD", "Auditorio"));

    private final List<Tramo> tramos = List.of(
            new Tramo("T01", "ENT", "BIB", PLANO, 120),
            new Tramo("T02", "ENT", "BLA", ESCALERA, 60),
            new Tramo("T03", "ENT", "PAR", PLANO, 80),
            new Tramo("T04", "BIB", "BLA", RAMPA, 70),
            new Tramo("T05", "BIB", "BLB", RAMPA, 90),
            new Tramo("T06", "BLA", "BLB", ESCALERA, 40),
            new Tramo("T07", "BLA", "CAF", PLANO, 50),
            new Tramo("T08", "CAF", "LAB", PLANO, 100),
            new Tramo("T09", "BLB", "LAB", RAMPA, 110),
            new Tramo("T10", "LAB", "AUD", ESCALERA, 30),
            new Tramo("T11", "PAR", "BIB", RAMPA, 150));

    @Override
    public List<Lugar> lugares() {
        return lugares;
    }

    @Override
    public Optional<Lugar> lugar(String id) {
        return lugares.stream().filter(l -> l.id().equals(id)).findFirst();
    }

    @Override
    public List<Tramo> tramos() {
        return tramos;
    }

    @Override
    public Optional<Tramo> tramo(String id) {
        return tramos.stream().filter(t -> t.id().equals(id)).findFirst();
    }
}
