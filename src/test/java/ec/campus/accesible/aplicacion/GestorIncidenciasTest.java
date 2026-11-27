package ec.campus.accesible.aplicacion;

import ec.campus.accesible.dominio.EstadoIncidencia;
import ec.campus.accesible.dominio.Incidencia;
import ec.campus.accesible.dominio.Preferencias;
import ec.campus.accesible.dominio.Tramo;
import ec.campus.accesible.dominio.TramoNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static ec.campus.accesible.dominio.TipoTramo.RAMPA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GestorIncidenciasTest {

    private static final ZoneId ECUADOR = ZoneId.of("America/Guayaquil");
    private final Clock reloj = Clock.fixed(Instant.parse("2026-11-26T15:00:00Z"), ECUADOR);

    private MapaDePrueba mapa;
    private IncidenciasDePrueba incidencias;
    private GestorIncidencias gestor;

    @BeforeEach
    void preparar() {
        mapa = new MapaDePrueba()
                .lugares("BIB", "BLB")
                .tramo("T05", "BIB", "BLB", RAMPA, 90);
        incidencias = new IncidenciasDePrueba();
        gestor = new GestorIncidencias(mapa, incidencias, reloj);
    }

    /**
     * Encontrado por Ángel en la revisión del PR #15: dos reportes al mismo tiempo recibían
     * el mismo ID y el segundo reemplazaba al primero. La barrera obliga a que los dos hilos
     * consulten el repositorio antes de que cualquiera guarde, que es el peor caso.
     */
    @Test
    @DisplayName("HU-06: dos reportes simultáneos reciben IDs distintos y se guardan los dos")
    void reportesSimultaneos() throws Exception {
        CyclicBarrier ambosLeyeron = new CyclicBarrier(2);
        IncidenciasDePrueba lenta = new IncidenciasDePrueba() {
            @Override
            public List<Incidencia> todas() {
                List<Incidencia> copia = super.todas();
                try {
                    ambosLeyeron.await(1, TimeUnit.SECONDS);
                } catch (Exception sinEspera) {
                    // si el gestor ya no consulta el repositorio para el ID, nadie espera aquí
                }
                return copia;
            }
        };
        GestorIncidencias gestorConcurrente = new GestorIncidencias(mapa, lenta, reloj);

        ExecutorService hilos = Executors.newFixedThreadPool(2);
        try {
            Future<Incidencia> primero = hilos.submit(() -> gestorConcurrente.reportar("T05", "Rampa bloqueada"));
            Future<Incidencia> segundo = hilos.submit(() -> gestorConcurrente.reportar("T05", "Charco en la rampa"));

            assertThat(primero.get(5, TimeUnit.SECONDS).id())
                    .isNotEqualTo(segundo.get(5, TimeUnit.SECONDS).id());
            assertThat(lenta.todas()).hasSize(2);
        } finally {
            hilos.shutdownNow();
        }
    }

    @Test
    @DisplayName("HU-06: un reporte válido queda REPORTADO con su fecha y se guarda")
    void reporteValido() {
        Incidencia incidencia = gestor.reportar("T05", "La rampa está bloqueada por material de obra");

        assertThat(incidencia.id()).isNotBlank();
        assertThat(incidencia.tramoId()).isEqualTo("T05");
        assertThat(incidencia.estado()).isEqualTo(EstadoIncidencia.REPORTADA);
        assertThat(incidencia.fechaReporte()).isEqualTo(LocalDateTime.of(2026, 11, 26, 10, 0));
        assertThat(incidencias.buscar(incidencia.id())).contains(incidencia);
    }

    @Test
    @DisplayName("HU-06: cada reporte recibe un identificador distinto")
    void identificadoresDistintos() {
        Incidencia primera = gestor.reportar("T05", "Rampa bloqueada");
        Incidencia segunda = gestor.reportar("T05", "Charco grande al pie de la rampa");

        assertThat(primera.id()).isNotEqualTo(segunda.id());
    }

    @Test
    @DisplayName("HU-06: un reporte sin validar no cierra el tramo ni cambia los recorridos")
    void reporteNoCierraElTramo() {
        gestor.reportar("T05", "La rampa está bloqueada");

        assertThat(mapa.tramo("T05").orElseThrow().disponible()).isTrue();
        assertThat(new PlanificadorRutas(mapa)
                .planificar("BIB", "BLB", Preferencias.evitarEscaleras())
                .tramos())
                .extracting(Tramo::id)
                .containsExactly("T05");
    }

    @Test
    @DisplayName("HU-06: un reporte sin descripción se rechaza indicando el dato que falta")
    void sinDescripcion() {
        assertThatThrownBy(() -> gestor.reportar("T05", "   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Falta la descripción de la barrera");
        assertThat(incidencias.todas()).isEmpty();
    }

    @Test
    @DisplayName("HU-06: un reporte sobre un tramo inexistente se rechaza")
    void tramoInexistente() {
        assertThatThrownBy(() -> gestor.reportar("T99", "Rampa bloqueada"))
                .isInstanceOf(TramoNoEncontradoException.class)
                .hasMessageContaining("T99");
        assertThat(incidencias.todas()).isEmpty();
    }
}
