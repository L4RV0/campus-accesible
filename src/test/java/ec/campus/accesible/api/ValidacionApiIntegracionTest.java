package ec.campus.accesible.api;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.contains;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** HU-07 y RNF-02 por HTTP, incluida la demo obligatoria del enunciado. */
@SpringBootTest
@AutoConfigureMockMvc
class ValidacionApiIntegracionTest {

    @Autowired
    MockMvc mvc;

    private String reportar(String tramoId) throws Exception {
        String json = mvc.perform(post("/api/incidencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tramoId\": \"" + tramoId + "\", \"descripcion\": \"Rampa bloqueada por obra\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(json, "$.id");
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @DisplayName("Demo: validar una incidencia en la rampa T05 recalcula Entrada → Bloque B de 210 m a 450 m")
    void demoValidarYRecalcular() throws Exception {
        mvc.perform(get("/api/recorridos").param("origen", "ENT").param("destino", "BLB").param("sinEscaleras", "true"))
                .andExpect(jsonPath("$.metrosTotales").value(210));

        String id = reportar("T05");
        mvc.perform(post("/api/incidencias/{id}/validacion", id).header("X-Rol", "GESTOR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("VALIDADA"))
                .andExpect(jsonPath("$.fechaRevision").exists());

        mvc.perform(get("/api/recorridos").param("origen", "ENT").param("destino", "BLB").param("sinEscaleras", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lugares[*].id").value(contains("ENT", "BIB", "BLA", "CAF", "LAB", "BLB")))
                .andExpect(jsonPath("$.metrosTotales").value(450));
    }

    @Test
    @DisplayName("RNF-02: sin el rol de gestor no se puede validar (403) y el tramo no cambia")
    void sinRolNoValida() throws Exception {
        String id = reportar("T05");

        mvc.perform(post("/api/incidencias/{id}/validacion", id))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.mensaje").value("Solo el gestor de infraestructura puede hacer esta operación"));
        mvc.perform(post("/api/incidencias/{id}/validacion", id).header("X-Rol", "VISITANTE"))
                .andExpect(status().isForbidden());

        mvc.perform(get("/api/recorridos").param("origen", "ENT").param("destino", "BLB").param("sinEscaleras", "true"))
                .andExpect(jsonPath("$.metrosTotales").value(210));
    }

    @Test
    @DisplayName("HU-07: rechazar responde RECHAZADA y una segunda decisión responde 409")
    void rechazarYLuegoValidarEsConflicto() throws Exception {
        String id = reportar("T04");

        mvc.perform(post("/api/incidencias/{id}/rechazo", id).header("X-Rol", "GESTOR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("RECHAZADA"));
        mvc.perform(post("/api/incidencias/{id}/validacion", id).header("X-Rol", "GESTOR"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value("La incidencia " + id + " está RECHAZADA y no puede pasar a VALIDADA"));
    }

    @Test
    @DisplayName("HU-07 · RNF-03: validar una incidencia que no existe responde 404")
    void incidenciaInexistente() throws Exception {
        mvc.perform(post("/api/incidencias/INC-999/validacion").header("X-Rol", "GESTOR"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("No existe la incidencia «INC-999»"));
    }
}
