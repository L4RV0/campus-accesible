package ec.campus.accesible.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.hamcrest.Matchers.contains;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** HU-10 (cambio de requisito) por HTTP con el mapa ficticio. */
@SpringBootTest
@AutoConfigureMockMvc
class CierresApiIntegracionTest {

    @Autowired
    MockMvc mvc;

    private static String cuerpo(LocalDateTime inicio, LocalDateTime fin) {
        return "{\"inicio\": \"" + inicio + "\", \"fin\": \"" + fin + "\"}";
    }

    private static LocalDateTime ahora() {
        return LocalDateTime.now(ZoneId.of("America/Guayaquil")).withNano(0);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @DisplayName("HU-10: con un cierre vigente en la rampa T05 la ruta sin escaleras pasa a 450 m y el mapa la marca cerrada")
    void cierreVigenteCambiaLaRuta() throws Exception {
        mvc.perform(post("/api/tramos/T05/cierres").header("X-Rol", "GESTOR")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(ahora().minusHours(1), ahora().plusDays(2))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tramoId").value("T05"));

        mvc.perform(get("/api/recorridos").param("origen", "ENT").param("destino", "BLB").param("sinEscaleras", "true"))
                .andExpect(jsonPath("$.lugares[*].id").value(contains("ENT", "BIB", "BLA", "CAF", "LAB", "BLB")))
                .andExpect(jsonPath("$.metrosTotales").value(450));
        mvc.perform(get("/api/mapa"))
                .andExpect(jsonPath("$.tramos[?(@.id == 'T05')].cerrado").value(contains(true)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @DisplayName("HU-10: un cierre ya vencido no afecta la ruta")
    void cierreVencidoNoAfecta() throws Exception {
        mvc.perform(post("/api/tramos/T05/cierres").header("X-Rol", "GESTOR")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(ahora().minusDays(3), ahora().minusDays(1))))
                .andExpect(status().isCreated());

        mvc.perform(get("/api/recorridos").param("origen", "ENT").param("destino", "BLB").param("sinEscaleras", "true"))
                .andExpect(jsonPath("$.metrosTotales").value(210));
    }

    @Test
    @DisplayName("HU-10 · RNF-03: fin anterior al inicio responde 400 con mensaje")
    void finAnteriorAlInicio() throws Exception {
        mvc.perform(post("/api/tramos/T05/cierres").header("X-Rol", "GESTOR")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(ahora().plusDays(2), ahora())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("La fecha de fin del cierre debe ser posterior a la de inicio"));
    }

    @Test
    @DisplayName("HU-10 · RNF-02: sin rol de gestor no se programa un cierre (403)")
    void sinRolNoProgramaCierre() throws Exception {
        mvc.perform(post("/api/tramos/T05/cierres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(ahora(), ahora().plusDays(1))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("HU-10 · RNF-03: un tramo inexistente responde 404")
    void tramoInexistente() throws Exception {
        mvc.perform(post("/api/tramos/T99/cierres").header("X-Rol", "GESTOR")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(ahora(), ahora().plusDays(1))))
                .andExpect(status().isNotFound());
    }
}
