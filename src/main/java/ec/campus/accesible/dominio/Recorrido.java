package ec.campus.accesible.dominio;

import java.util.List;

/** Resultado de planificar: los lugares en orden, los tramos usados y la distancia total. */
public record Recorrido(List<Lugar> lugares, List<Tramo> tramos) {

    public int metrosTotales() {
        return tramos.stream().mapToInt(Tramo::metros).sum();
    }
}
