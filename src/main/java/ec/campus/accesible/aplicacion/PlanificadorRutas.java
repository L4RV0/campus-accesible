package ec.campus.accesible.aplicacion;

import ec.campus.accesible.dominio.Lugar;
import ec.campus.accesible.dominio.LugarNoEncontradoException;
import ec.campus.accesible.dominio.MotivoSinRecorrido;
import ec.campus.accesible.dominio.Preferencias;
import ec.campus.accesible.dominio.Recorrido;
import ec.campus.accesible.dominio.SinRecorridoCompatibleException;
import ec.campus.accesible.dominio.TipoTramo;
import ec.campus.accesible.dominio.Tramo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;

/** Calcula el recorrido más corto (Dijkstra) sobre el grafo del campus. */
public class PlanificadorRutas implements IPlanificadorRutas {

    private final IMapaRepository mapa;

    public PlanificadorRutas(IMapaRepository mapa) {
        this.mapa = mapa;
    }

    @Override
    public Recorrido planificar(String origenId, String destinoId, Preferencias preferencias) {
        Lugar origen = buscarLugar(origenId);
        Lugar destino = buscarLugar(destinoId);

        Optional<Recorrido> recorrido = buscar(origen, destino, preferencias.sinEscaleras(), true);
        if (recorrido.isPresent()) {
            return recorrido.get();
        }

        List<MotivoSinRecorrido> motivos = new ArrayList<>();
        String mensaje;
        if (preferencias.sinEscaleras() && buscar(origen, destino, false, true).isPresent()) {
            motivos.add(MotivoSinRecorrido.ESCALERAS);
            mensaje = "No hay un recorrido sin escaleras disponible entre "
                    + origen.nombre() + " y " + destino.nombre();
        } else if (buscar(origen, destino, preferencias.sinEscaleras(), false).isPresent()) {
            motivos.add(MotivoSinRecorrido.TRAMOS_CERRADOS);
            mensaje = "No hay un recorrido disponible entre "
                    + origen.nombre() + " y " + destino.nombre() + ": hay tramos cerrados";
        } else if (buscar(origen, destino, false, false).isPresent()) {
            motivos.add(MotivoSinRecorrido.ESCALERAS);
            motivos.add(MotivoSinRecorrido.TRAMOS_CERRADOS);
            mensaje = "No hay un recorrido sin escaleras disponible entre "
                    + origen.nombre() + " y " + destino.nombre() + ": hay tramos cerrados";
        } else {
            motivos.add(MotivoSinRecorrido.SIN_CONEXION);
            mensaje = "No hay un recorrido disponible entre "
                    + origen.nombre() + " y " + destino.nombre() + ": los lugares no están conectados";
        }
        throw new SinRecorridoCompatibleException(mensaje, motivos);
    }

    private Optional<Recorrido> buscar(Lugar origen, Lugar destino,
                                       boolean evitarEscaleras, boolean respetarCierres) {
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
                if (!tramo.conecta(actual)) {
                    continue;
                }
                if (evitarEscaleras && tramo.tipo() == TipoTramo.ESCALERA) {
                    continue;
                }
                if (respetarCierres && tramo.cerrado()) {
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
