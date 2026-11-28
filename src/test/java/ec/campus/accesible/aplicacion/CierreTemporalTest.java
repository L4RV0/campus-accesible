package ec.campus.accesible.aplicacion;

import ec.campus.accesible.dominio.CierreTemporal;
import ec.campus.accesible.dominio.Preferencias;
import ec.campus.accesible.dominio.Tramo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static ec.campus.accesible.dominio.TipoTramo.PLANO;
import static ec.campus.accesible.dominio.TipoTramo.RAMPA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * HU-10 · Cambio de requisito: cierre temporal de una rampa.
 *
 *   BIB ──(rampa 90, T05)── BLB
 *    └─(plano 100)─ CAF ─(plano 100)─┘
 */
class CierreTemporalTest {

    private static final ZoneId ECUADOR = ZoneId.of("America/Guayaquil");
    private static final LocalDateTime INICIO = LocalDateTime.of(2026, 11, 28, 8, 0);
    private static final LocalDateTime FIN = LocalDateTime.of(2026, 11, 30, 18, 0);

    private MapaDePrueba mapa;

    @BeforeEach
    void preparar() {
        mapa = new MapaDePrueba()
                .lugares("BIB", "BLB", "CAF")
                .tramo("T05", "BIB", "BLB", RAMPA, 90)
                .tramo("BC", "BIB", "CAF", PLANO, 100)
                .tramo("CB", "CAF", "BLB", PLANO, 100);
        mapa.tramo("T05").orElseThrow().agregarCierre(new CierreTemporal(INICIO, FIN));
    }

    private PlanificadorRutas planificadorEn(LocalDateTime momento) {
        return new PlanificadorRutas(mapa, Clock.fixed(momento.atZone(ECUADOR).toInstant(), ECUADOR));
    }

    private Object tramosDeBibliotecaABloqueB(LocalDateTime momento) {
        return planificadorEn(momento).planificar("BIB", "BLB", Preferencias.evitarEscaleras())
                .tramos().stream().map(Tramo::id).toList();
    }

    @Test
    @DisplayName("HU-10: durante el cierre la rampa no se usa")
    void duranteElCierreNoSeUsaLaRampa() {
        assertThat(tramosDeBibliotecaABloqueB(LocalDateTime.of(2026, 11, 29, 12, 0)))
                .isEqualTo(java.util.List.of("BC", "CB"));
    }

    @Test
    @DisplayName("HU-10: al terminar el plazo la rampa vuelve sola, sin intervención del gestor")
    void alTerminarElCierreVuelveLaRampa() {
        assertThat(tramosDeBibliotecaABloqueB(FIN)).isEqualTo(java.util.List.of("T05"));
    }

    @Test
    @DisplayName("HU-10: el cierre empieza exactamente en su fecha de inicio")
    void elInicioEstaIncluido() {
        assertThat(tramosDeBibliotecaABloqueB(INICIO)).isEqualTo(java.util.List.of("BC", "CB"));
        assertThat(tramosDeBibliotecaABloqueB(INICIO.minusMinutes(1))).isEqualTo(java.util.List.of("T05"));
    }

    @Test
    @DisplayName("HU-10: un cierre con fin anterior o igual al inicio se rechaza")
    void finAnteriorAlInicioSeRechaza() {
        assertThatThrownBy(() -> new CierreTemporal(FIN, INICIO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La fecha de fin del cierre debe ser posterior a la de inicio");
        assertThatThrownBy(() -> new CierreTemporal(INICIO, INICIO))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
