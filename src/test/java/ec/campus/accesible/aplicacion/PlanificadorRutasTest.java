package ec.campus.accesible.aplicacion;

import ec.campus.accesible.dominio.Lugar;
import ec.campus.accesible.dominio.LugarNoEncontradoException;
import ec.campus.accesible.dominio.MotivoSinRecorrido;
import ec.campus.accesible.dominio.SinRecorridoCompatibleException;
import ec.campus.accesible.dominio.Preferencias;
import ec.campus.accesible.dominio.Recorrido;
import ec.campus.accesible.dominio.Tramo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static ec.campus.accesible.dominio.TipoTramo.ESCALERA;
import static ec.campus.accesible.dominio.TipoTramo.PLANO;
import static ec.campus.accesible.dominio.TipoTramo.RAMPA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlanificadorRutasTest {

    /*
     *   A ──(plano 100)── B ──(plano 100)── C
     *   └──────────(rampa 250)──────────────┘
     */
    @Test
    @DisplayName("HU-02: elige el camino más corto entre origen y destino")
    void eligeElCaminoMasCorto() {
        MapaDePrueba mapa = new MapaDePrueba()
                .lugares("A", "B", "C")
                .tramo("AB", "A", "B", PLANO, 100)
                .tramo("BC", "B", "C", PLANO, 100)
                .tramo("AC", "A", "C", RAMPA, 250);

        Recorrido recorrido = new PlanificadorRutas(mapa).planificar("A", "C", Preferencias.ninguna());

        assertThat(recorrido.lugares()).extracting(Lugar::id).containsExactly("A", "B", "C");
        assertThat(recorrido.metrosTotales()).isEqualTo(200);
    }

    @Test
    @DisplayName("HU-02: los tramos se pueden recorrer en ambos sentidos")
    void losTramosSonBidireccionales() {
        MapaDePrueba mapa = new MapaDePrueba()
                .lugares("A", "B")
                .tramo("AB", "A", "B", PLANO, 80);

        Recorrido recorrido = new PlanificadorRutas(mapa).planificar("B", "A", Preferencias.ninguna());

        assertThat(recorrido.lugares()).extracting(Lugar::id).containsExactly("B", "A");
    }

    @Test
    @DisplayName("HU-02: si origen y destino coinciden, el recorrido es de 0 m")
    void origenIgualADestino() {
        MapaDePrueba mapa = new MapaDePrueba().lugares("A");

        Recorrido recorrido = new PlanificadorRutas(mapa).planificar("A", "A", Preferencias.ninguna());

        assertThat(recorrido.lugares()).extracting(Lugar::id).containsExactly("A");
        assertThat(recorrido.tramos()).isEmpty();
        assertThat(recorrido.metrosTotales()).isZero();
    }

    /*
     *   A ──(escalera 40)── B
     *   └─(rampa 90)─ C ─(plano 30)─┘
     */
    @Test
    @DisplayName("Regla 1 · HU-03: «sin escaleras» excluye la escalera aunque sea más corta")
    void sinEscalerasExcluyeEscaleras() {
        MapaDePrueba mapa = new MapaDePrueba()
                .lugares("A", "B", "C")
                .tramo("AB", "A", "B", ESCALERA, 40)
                .tramo("AC", "A", "C", RAMPA, 90)
                .tramo("CB", "C", "B", PLANO, 30);

        Recorrido recorrido = new PlanificadorRutas(mapa).planificar("A", "B", Preferencias.evitarEscaleras());

        assertThat(recorrido.tramos()).extracting(Tramo::tipo).doesNotContain(ESCALERA);
        assertThat(recorrido.lugares()).extracting(Lugar::id).containsExactly("A", "C", "B");
    }

    @Test
    @DisplayName("Regla 1 · HU-03: sin la preferencia, la escalera se usa si es más corta")
    void sinPreferenciaUsaEscalera() {
        MapaDePrueba mapa = new MapaDePrueba()
                .lugares("A", "B", "C")
                .tramo("AB", "A", "B", ESCALERA, 40)
                .tramo("AC", "A", "C", RAMPA, 90)
                .tramo("CB", "C", "B", PLANO, 30);

        Recorrido recorrido = new PlanificadorRutas(mapa).planificar("A", "B", Preferencias.ninguna());

        assertThat(recorrido.tramos()).extracting(Tramo::id).containsExactly("AB");
    }

    /*
     *   A ──(rampa 50, CERRADA)── B
     *   └─(plano 70)─ C ─(plano 70)─┘
     */
    @Test
    @DisplayName("Regla 2 · HU-04: un tramo cerrado no se recomienda")
    void tramoCerradoNoSeRecomienda() {
        MapaDePrueba mapa = new MapaDePrueba()
                .lugares("A", "B", "C")
                .tramo("AB", "A", "B", RAMPA, 50)
                .tramo("AC", "A", "C", PLANO, 70)
                .tramo("CB", "C", "B", PLANO, 70);
        mapa.tramo("AB").orElseThrow().cerrar();

        Recorrido recorrido = new PlanificadorRutas(mapa).planificar("A", "B", Preferencias.ninguna());

        assertThat(recorrido.tramos()).extracting(Tramo::id).containsExactly("AC", "CB");
    }

    @Test
    @DisplayName("Regla 2 · HU-04: al reabrir el tramo vuelve a recomendarse")
    void tramoReabiertoVuelveAUsarse() {
        MapaDePrueba mapa = new MapaDePrueba()
                .lugares("A", "B", "C")
                .tramo("AB", "A", "B", RAMPA, 50)
                .tramo("AC", "A", "C", PLANO, 70)
                .tramo("CB", "C", "B", PLANO, 70);
        Tramo rampa = mapa.tramo("AB").orElseThrow();
        rampa.cerrar();
        rampa.abrir();

        Recorrido recorrido = new PlanificadorRutas(mapa).planificar("A", "B", Preferencias.ninguna());

        assertThat(recorrido.tramos()).extracting(Tramo::id).containsExactly("AB");
    }

    @Test
    @DisplayName("Regla 3 · HU-05: si solo se llega por escalera, se informa que no hay ruta sin escaleras")
    void sinRutaPorEscaleras() {
        MapaDePrueba mapa = new MapaDePrueba()
                .lugares("Biblioteca", "Auditorio")
                .tramo("BA", "Biblioteca", "Auditorio", ESCALERA, 30);

        assertThatThrownBy(() -> new PlanificadorRutas(mapa)
                .planificar("Biblioteca", "Auditorio", Preferencias.evitarEscaleras()))
                .isInstanceOf(SinRecorridoCompatibleException.class)
                .hasMessage("No hay un recorrido sin escaleras disponible entre Biblioteca y Auditorio")
                .satisfies(e -> assertThat(((SinRecorridoCompatibleException) e).motivos())
                        .containsExactly(MotivoSinRecorrido.ESCALERAS));
    }

    @Test
    @DisplayName("Regla 3 · HU-05: si el único tramo está cerrado, se informa el motivo")
    void sinRutaPorTramoCerrado() {
        MapaDePrueba mapa = new MapaDePrueba()
                .lugares("Biblioteca", "Auditorio")
                .tramo("BA", "Biblioteca", "Auditorio", RAMPA, 30);
        mapa.tramo("BA").orElseThrow().cerrar();

        assertThatThrownBy(() -> new PlanificadorRutas(mapa)
                .planificar("Biblioteca", "Auditorio", Preferencias.ninguna()))
                .isInstanceOf(SinRecorridoCompatibleException.class)
                .hasMessage("No hay un recorrido disponible entre Biblioteca y Auditorio: hay tramos cerrados")
                .satisfies(e -> assertThat(((SinRecorridoCompatibleException) e).motivos())
                        .containsExactly(MotivoSinRecorrido.TRAMOS_CERRADOS));
    }

    @Test
    @DisplayName("Regla 3 · HU-05: lugares sin ninguna conexión")
    void lugaresSinConexion() {
        MapaDePrueba mapa = new MapaDePrueba().lugares("A", "B");

        assertThatThrownBy(() -> new PlanificadorRutas(mapa).planificar("A", "B", Preferencias.ninguna()))
                .isInstanceOf(SinRecorridoCompatibleException.class)
                .hasMessage("No hay un recorrido disponible entre A y B: los lugares no están conectados")
                .satisfies(e -> assertThat(((SinRecorridoCompatibleException) e).motivos())
                        .containsExactly(MotivoSinRecorrido.SIN_CONEXION));
    }

    @Test
    @DisplayName("HU-02: un lugar inexistente se rechaza con un mensaje claro")
    void lugarInexistente() {
        MapaDePrueba mapa = new MapaDePrueba().lugares("A");

        assertThatThrownBy(() -> new PlanificadorRutas(mapa).planificar("A", "Z", Preferencias.ninguna()))
                .isInstanceOf(LugarNoEncontradoException.class)
                .hasMessageContaining("Z");
    }
}
