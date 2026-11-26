package ec.campus.accesible.aplicacion;

import ec.campus.accesible.dominio.Lugar;
import ec.campus.accesible.dominio.LugarNoEncontradoException;
import ec.campus.accesible.dominio.MotivoSinRecorrido;
import ec.campus.accesible.dominio.Preferencias;
import ec.campus.accesible.dominio.Recorrido;
import ec.campus.accesible.dominio.SinRecorridoCompatibleException;
import ec.campus.accesible.dominio.Tramo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.function.Predicate;

/**
 * Calcula el recorrido más corto (Dijkstra) usando solo los tramos que el visitante puede usar.
 * Si no hay ninguno, vuelve a buscar relajando cada restricción para explicar el motivo (regla 3).
 */
public class PlanificadorRutas implements IPlanificadorRutas {

    private final IMapaRepository mapa;

    public PlanificadorRutas(IMapaRepository mapa) {
        this.mapa = mapa;
    }

    @Override
    public Recorrido planificar(String origenId, String destinoId, Preferencias preferencias) {
        Lugar origen = buscarLugar(origenId);
        Lugar destino = buscarLugar(destinoId);

        Predicate<Tramo> respetaPreferencias = preferencias::admite;
        Predicate<Tramo> abierto = Tramo::disponible;

        return buscar(origen, destino, respetaPreferencias.and(abierto))
                .orElseThrow(() -> sinRecorrido(origen, destino, respetaPreferencias, abierto));
    }

    private SinRecorridoCompatibleException sinRecorrido(Lugar origen, Lugar destino,
                                                         Predicate<Tramo> respetaPreferencias,
                                                         Predicate<Tramo> abierto) {
        List<MotivoSinRecorrido> motivos = new ArrayList<>();
        if (buscar(origen, destino, abierto).isPresent()) {
            motivos.add(MotivoSinRecorrido.ESCALERAS);
        } else if (buscar(origen, destino, respetaPreferencias).isPresent()) {
            motivos.add(MotivoSinRecorrido.TRAMOS_CERRADOS);
        } else if (buscar(origen, destino, tramo -> true).isPresent()) {
            motivos.add(MotivoSinRecorrido.ESCALERAS);
            motivos.add(MotivoSinRecorrido.TRAMOS_CERRADOS);
        } else {
            motivos.add(MotivoSinRecorrido.SIN_CONEXION);
        }
        return new SinRecorridoCompatibleException(mensaje(origen, destino, motivos), motivos);
    }

    private static String mensaje(Lugar origen, Lugar destino, List<MotivoSinRecorrido> motivos) {
        String entre = " disponible entre " + origen.nombre() + " y " + destino.nombre();
        String inicio = motivos.contains(MotivoSinRecorrido.ESCALERAS)
                ? "No hay un recorrido sin escaleras" + entre
                : "No hay un recorrido" + entre;
        if (motivos.contains(MotivoSinRecorrido.TRAMOS_CERRADOS)) {
            return inicio + ": hay tramos cerrados";
        }
        if (motivos.contains(MotivoSinRecorrido.SIN_CONEXION)) {
            return inicio + ": los lugares no están conectados";
        }
        return inicio;
    }

    private Optional<Recorrido> buscar(Lugar origen, Lugar destino, Predicate<Tramo> usable) {
        Map<String, Integer> distancia = new HashMap<>();
        Map<String, Tramo> llegoPor = new HashMap<>();
        PriorityQueue<String> pendientes =
                new PriorityQueue<>(Comparator.comparingInt(distancia::get));

        distancia.put(origen.id(), 0);
        pendientes.add(origen.id());

        while (!pendientes.isEmpty()) {
            String actual = pendientes.poll();
            if (actual.equals(destino.id())) {
                return Optional.of(reconstruir(origen, destino, llegoPor));
            }
            for (Tramo tramo : mapa.tramos()) {
                if (!tramo.conecta(actual) || !usable.test(tramo)) {
                    continue;
                }
                String vecino = tramo.otroExtremo(actual);
                int nuevaDistancia = distancia.get(actual) + tramo.metros();
                if (nuevaDistancia < distancia.getOrDefault(vecino, Integer.MAX_VALUE)) {
                    pendientes.remove(vecino);
                    distancia.put(vecino, nuevaDistancia);
                    llegoPor.put(vecino, tramo);
                    pendientes.add(vecino);
                }
            }
        }
        return Optional.empty();
    }

    private Recorrido reconstruir(Lugar origen, Lugar destino, Map<String, Tramo> llegoPor) {
        List<Lugar> lugares = new ArrayList<>();
        List<Tramo> tramos = new ArrayList<>();
        String actual = destino.id();
        lugares.add(destino);
        while (!actual.equals(origen.id())) {
            Tramo tramo = llegoPor.get(actual);
            tramos.add(tramo);
            actual = tramo.otroExtremo(actual);
            lugares.add(buscarLugar(actual));
        }
        Collections.reverse(lugares);
        Collections.reverse(tramos);
        return new Recorrido(lugares, tramos);
    }

    private Lugar buscarLugar(String id) {
        return mapa.lugar(id).orElseThrow(() -> new LugarNoEncontradoException(id));
    }
}
