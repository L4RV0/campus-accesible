package ec.campus.accesible.aplicacion;

import ec.campus.accesible.dominio.Lugar;
import ec.campus.accesible.dominio.Tramo;

import java.util.List;
import java.util.Optional;

/** Puerto hacia los datos del mapa. La implementación vive en infraestructura. */
public interface IMapaRepository {

    List<Lugar> lugares();

    Optional<Lugar> lugar(String id);

    List<Tramo> tramos();

    Optional<Tramo> tramo(String id);
}
