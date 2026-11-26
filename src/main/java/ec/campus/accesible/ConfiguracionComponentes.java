package ec.campus.accesible;

import ec.campus.accesible.aplicacion.IMapaRepository;
import ec.campus.accesible.aplicacion.IPlanificadorRutas;
import ec.campus.accesible.aplicacion.PlanificadorRutas;
import ec.campus.accesible.infraestructura.MapaCampusEnMemoria;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Único lugar donde se conectan los componentes. Las capas de aplicación y dominio
 * no conocen Spring; así se pueden probar sin levantar el servidor.
 */
@Configuration
public class ConfiguracionComponentes {

    @Bean
    IMapaRepository mapaRepository() {
        return new MapaCampusEnMemoria();
    }

    @Bean
    IPlanificadorRutas planificadorRutas(IMapaRepository mapa) {
        return new PlanificadorRutas(mapa);
    }
}
