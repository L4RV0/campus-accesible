package ec.campus.accesible.aplicacion;

import ec.campus.accesible.dominio.Lugar;
import ec.campus.accesible.dominio.TipoTramo;
import ec.campus.accesible.dominio.Tramo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Mapa pequeño armado en cada prueba, para que el escenario se lea en la propia prueba. */
class MapaDePrueba implements IMapaRepository {

    private final List<Lugar> lugares = new ArrayList<>();
    private final List<Tramo> tramos = new ArrayList<>();

    MapaDePrueba lugares(String... ids) {
        for (String id : ids) {
            lugares.add(new Lugar(id, id));
        }
        return this;
    }

    MapaDePrueba tramo(String id, String a, String b, TipoTramo tipo, int metros) {
        tramos.add(new Tramo(id, a, b, tipo, metros));
        return this;
    }

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
