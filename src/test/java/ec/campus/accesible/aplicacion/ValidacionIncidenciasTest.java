package ec.campus.accesible.aplicacion;

import ec.campus.accesible.dominio.EstadoIncidencia;
import ec.campus.accesible.dominio.Incidencia;
import ec.campus.accesible.dominio.IncidenciaNoEncontradaException;
import ec.campus.accesible.dominio.Preferencias;
import ec.campus.accesible.dominio.Tramo;
import ec.campus.accesible.dominio.TransicionInvalidaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static ec.campus.accesible.dominio.TipoTramo.PLANO;
import static ec.campus.accesible.dominio.TipoTramo.RAMPA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** HU-07: el gestor valida o rechaza los reportes; solo una barrera confirmada cambia los recorridos. */
class ValidacionIncidenciasTest {

    private final Clock reloj = Clock.fixed(Instant.parse("2026-11-27T15:00:00Z"), ZoneId.of("America/Guayaquil"));

    private MapaDePrueba mapa;
    private GestorIncidencias gestor;

    /*
     *   BIB ──(rampa 90, T05)── BLB
     *    └─(plano 100)─ CAF ─(plano 100)─┘
     */
    @BeforeEach
    void preparar() {
        mapa = new MapaDePrueba()
                .lugares("BIB", "BLB", "CAF")
                .tramo("T05", "BIB", "BLB", RAMPA, 90)
                .tramo("BC", "BIB", "CAF", PLANO, 100)
                .tramo("CB", "CAF", "BLB", PLANO, 100);
        gestor = new GestorIncidencias(mapa, new IncidenciasDePrueba(), reloj);
    }

    @Test
    @DisplayName("HU-07: validar deja la incidencia VALIDADA con fecha de revisión y cierra el tramo")
    void validarCierraElTramo() {
        Incidencia reporte = gestor.reportar("T05", "Rampa bloqueada por obra");

        Incidencia validada = gestor.validar(reporte.id());

        assertThat(validada.estado()).isEqualTo(EstadoIncidencia.VALIDADA);
        assertThat(validada.fechaRevision()).isEqualTo(LocalDateTime.of(2026, 11, 27, 10, 0));
        assertThat(mapa.tramo("T05").orElseThrow().disponible()).isFalse();
    }

    @Test
    @DisplayName("HU-07: tras validar, el recorrido se recalcula sin el tramo bloqueado")
    void validarRecalculaElRecorrido() {
        Incidencia reporte = gestor.reportar("T05", "Rampa bloqueada por obra");
        PlanificadorRutas planificador = new PlanificadorRutas(mapa);
        assertThat(planificador.planificar("BIB", "BLB", Preferencias.evitarEscaleras()).metrosTotales())
                .isEqualTo(90);

        gestor.validar(reporte.id());

        assertThat(planificador.planificar("BIB", "BLB", Preferencias.evitarEscaleras()).tramos())
                .extracting(Tramo::id).containsExactly("BC", "CB");
    }

    @Test
    @DisplayName("HU-07: rechazar deja la incidencia RECHAZADA y el tramo sigue abierto")
    void rechazarNoCambiaElTramo() {
        Incidencia reporte = gestor.reportar("T05", "Me pareció ver un charco");

        Incidencia rechazada = gestor.rechazar(reporte.id());

        assertThat(rechazada.estado()).isEqualTo(EstadoIncidencia.RECHAZADA);
        assertThat(rechazada.fechaRevision()).isNotNull();
        assertThat(mapa.tramo("T05").orElseThrow().disponible()).isTrue();
    }

    @Test
    @DisplayName("HU-07: una incidencia ya rechazada no se puede validar")
    void noSeValidaUnaRechazada() {
        Incidencia reporte = gestor.reportar("T05", "Me pareció ver un charco");
        gestor.rechazar(reporte.id());

        assertThatThrownBy(() -> gestor.validar(reporte.id()))
                .isInstanceOf(TransicionInvalidaException.class)
                .hasMessage("La incidencia " + reporte.id() + " está RECHAZADA y no puede pasar a VALIDADA");
        assertThat(mapa.tramo("T05").orElseThrow().disponible()).isTrue();
    }

    @Test
    @DisplayName("HU-07: validar una incidencia inexistente se rechaza con mensaje claro")
    void incidenciaInexistente() {
        assertThatThrownBy(() -> gestor.validar("INC-999"))
                .isInstanceOf(IncidenciaNoEncontradaException.class)
                .hasMessageContaining("INC-999");
    }
}
