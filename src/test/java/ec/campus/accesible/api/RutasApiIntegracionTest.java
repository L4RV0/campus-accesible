package ec.campus.accesible.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Prueba de integración: levanta la aplicación completa con el mapa ficticio y la recorre por HTTP. */
@SpringBootTest
@AutoConfigureMockMvc
class RutasApiIntegracionTest {

    @Autowired
    MockMvc mvc;

    @Test
    @DisplayName("HU-01: el mapa devuelve los 8 lugares y 11 tramos del campus ficticio")
    void mapaCompleto() throws Exception {
        mvc.perform(get("/api/mapa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lugares", hasSize(8)))
                .andExpect(jsonPath("$.tramos", hasSize(11)));
    }

    @Test
    @DisplayName("HU-01: la página del mapa se sirve en la raíz con su formulario etiquetado")
    void paginaDelMapa() throws Exception {
        mvc.perform(get("/index.html"))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                        .string(org.hamcrest.Matchers.allOf(
                                org.hamcrest.Matchers.containsString("<label for=\"origen\">"),
                                org.hamcrest.Matchers.containsString("<label for=\"destino\">"),
                                org.hamcrest.Matchers.containsString("<strong>ficticios</strong>"))));
    }

    @Test
    @DisplayName("HU-03: de la Entrada al Bloque B sin escaleras no pasa por ninguna escalera")
    void recorridoSinEscaleras() throws Exception {
        mvc.perform(get("/api/recorridos")
                        .param("origen", "ENT").param("destino", "BLB").param("sinEscaleras", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tramos[*].tipo", everyItem(not("ESCALERA"))))
                .andExpect(jsonPath("$.lugares[*].id").value(org.hamcrest.Matchers.contains("ENT", "BIB", "BLB")))
                .andExpect(jsonPath("$.metrosTotales").value(210))
                .andExpect(jsonPath("$.aviso").value(RutasController.AVISO));
    }

    @Test
    @DisplayName("HU-05: al Auditorio solo se llega por escalera, la API lo explica con 422")
    void auditorioSinEscalerasNoTieneRuta() throws Exception {
        mvc.perform(get("/api/recorridos")
                        .param("origen", "ENT").param("destino", "AUD").param("sinEscaleras", "true"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.mensaje")
                        .value("No hay un recorrido sin escaleras disponible entre Entrada principal y Auditorio"))
                .andExpect(jsonPath("$.motivos[0]").value("ESCALERAS"));
    }

    @Test
    @DisplayName("RNF-03: un lugar inexistente responde 404 con mensaje, sin traza")
    void lugarInexistente() throws Exception {
        mvc.perform(get("/api/recorridos").param("origen", "ENT").param("destino", "XYZ"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("No existe el lugar «XYZ» en el mapa"))
                .andExpect(jsonPath("$.trace").doesNotExist());
    }

    @Test
    @DisplayName("RNF-03: si falta el destino responde 400 indicando qué dato falta")
    void faltaDestino() throws Exception {
        mvc.perform(get("/api/recorridos").param("origen", "ENT"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Falta el dato «destino»"));
    }
}
