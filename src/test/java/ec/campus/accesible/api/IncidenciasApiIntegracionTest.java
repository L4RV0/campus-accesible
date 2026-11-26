package ec.campus.accesible.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** HU-06 de punta a punta por HTTP, con el mapa ficticio real. */
@SpringBootTest
@AutoConfigureMockMvc
class IncidenciasApiIntegracionTest {

    @Autowired
    MockMvc mvc;

    @Test
    @DisplayName("HU-06: reportar una barrera responde 201 con la incidencia REPORTADA")
    void reportarBarrera() throws Exception {
        mvc.perform(post("/api/incidencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tramoId": "T05", "descripcion": "Rampa bloqueada por material de obra"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", startsWith("INC-")))
                .andExpect(jsonPath("$.tramoId").value("T05"))
                .andExpect(jsonPath("$.estado").value("REPORTADA"))
                .andExpect(jsonPath("$.fechaReporte", notNullValue()));
    }

    @Test
    @DisplayName("HU-06: tras reportar (sin validar) la ruta sin escaleras sigue usando la rampa")
    void reporteSinValidarNoCambiaLaRuta() throws Exception {
        mvc.perform(post("/api/incidencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tramoId\": \"T05\", \"descripcion\": \"Rampa con charco\"}"))
                .andExpect(status().isCreated());

        mvc.perform(get("/api/recorridos")
                        .param("origen", "ENT").param("destino", "BLB").param("sinEscaleras", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lugares[*].id").value(contains("ENT", "BIB", "BLB")))
                .andExpect(jsonPath("$.metrosTotales").value(210));
    }

    @Test
    @DisplayName("HU-06 · RNF-03: sin descripción responde 400 indicando qué falta")
    void sinDescripcion() throws Exception {
        mvc.perform(post("/api/incidencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tramoId\": \"T05\", \"descripcion\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Falta la descripción de la barrera"));
    }

    @Test
    @DisplayName("HU-06 · RNF-03: un tramo inexistente responde 404")
    void tramoInexistente() throws Exception {
        mvc.perform(post("/api/incidencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tramoId\": \"T99\", \"descripcion\": \"Rampa bloqueada\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("No existe el tramo «T99» en el mapa"));
    }

    @Test
    @DisplayName("RNF-03: un cuerpo que no es JSON responde 400 sin traza")
    void cuerpoInvalido() throws Exception {
        mvc.perform(post("/api/incidencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("esto no es json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Los datos enviados no tienen el formato esperado"));
    }
}
