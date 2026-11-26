package ec.campus.accesible.aplicacion;

import ec.campus.accesible.dominio.Lugar;
import ec.campus.accesible.dominio.LugarNoEncontradoException;
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

    @Test
    @DisplayName("HU-02: un lugar inexistente se rechaza con un mensaje claro")
    void lugarInexistente() {
        MapaDePrueba mapa = new MapaDePrueba().lugares("A");

        assertThatThrownBy(() -> new PlanificadorRutas(mapa).planificar("A", "Z", Preferencias.ninguna()))
                .isInstanceOf(LugarNoEncontradoException.class)
                .hasMessageContaining("Z");
    }
}
