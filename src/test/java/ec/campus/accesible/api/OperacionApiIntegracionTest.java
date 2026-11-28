package ec.campus.accesible.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** DevOps: la aplicación desplegada dice qué versión es y si está viva, sin exponer nada más. */
@SpringBootTest
@AutoConfigureMockMvc
class OperacionApiIntegracionTest {

    @Autowired
    MockMvc mvc;

    @Test
    @DisplayName("DevOps: /actuator/health responde UP")
    void saludDelServicio() throws Exception {
        mvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    @DisplayName("DevOps: /actuator/info muestra la versión construida")
    void versionDesplegada() throws Exception {
        mvc.perform(get("/actuator/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.build.artifact").value("campus-accesible"))
                .andExpect(jsonPath("$.build.version").value("1.0.0"));
    }

    @Test
    @DisplayName("DevOps: no se exponen otros endpoints de administración")
    void sinEndpointsSensibles() throws Exception {
        mvc.perform(get("/actuator/env")).andExpect(status().isNotFound());
        mvc.perform(get("/actuator/beans")).andExpect(status().isNotFound());
    }
}
