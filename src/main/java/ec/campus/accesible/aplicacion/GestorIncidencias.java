package ec.campus.accesible.aplicacion;

import ec.campus.accesible.dominio.Incidencia;
import ec.campus.accesible.dominio.IncidenciaNoEncontradaException;
import ec.campus.accesible.dominio.TramoNoEncontradoException;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

public class GestorIncidencias implements IGestorIncidencias {

    private final IMapaRepository mapa;
    private final IIncidenciaRepository incidencias;
    private final Clock reloj;
    private final AtomicLong contador = new AtomicLong();

    public GestorIncidencias(IMapaRepository mapa, IIncidenciaRepository incidencias, Clock reloj) {
        this.mapa = mapa;
        this.incidencias = incidencias;
        this.reloj = reloj;
    }

    @Override
    public Incidencia reportar(String tramoId, String descripcion) {
        if (mapa.tramo(tramoId).isEmpty()) {
            throw new TramoNoEncontradoException(tramoId);
        }
        Incidencia incidencia = new Incidencia(siguienteId(), tramoId, descripcion, LocalDateTime.now(reloj));
        return incidencias.guardar(incidencia);
    }

    /**
     * Validar y cerrar el tramo se hacen juntos y de a uno (synchronized), para que nunca quede
     * una incidencia VALIDADA con su tramo abierto (riesgo señalado en el ADR-0001).
     */
    @Override
    public synchronized Incidencia validar(String incidenciaId) {
        Incidencia incidencia = buscar(incidenciaId);
        incidencia.validar(LocalDateTime.now(reloj));
        mapa.tramo(incidencia.tramoId()).orElseThrow(() -> new TramoNoEncontradoException(incidencia.tramoId()))
                .cerrar();
        return incidencias.guardar(incidencia);
    }

    @Override
    public synchronized Incidencia rechazar(String incidenciaId) {
        Incidencia incidencia = buscar(incidenciaId);
        incidencia.rechazar(LocalDateTime.now(reloj));
        return incidencias.guardar(incidencia);
    }

    private Incidencia buscar(String incidenciaId) {
        return incidencias.buscar(incidenciaId)
                .orElseThrow(() -> new IncidenciaNoEncontradaException(incidenciaId));
    }

    /**
     * El contador se incrementa de forma atómica: dos reportes simultáneos nunca reciben el mismo ID.
     * Antes se usaba «cantidad guardada + 1», que fallaba con reportes al mismo tiempo (revisión del PR #15).
     */
    private String siguienteId() {
        return String.format("INC-%03d", contador.incrementAndGet());
    }
}
