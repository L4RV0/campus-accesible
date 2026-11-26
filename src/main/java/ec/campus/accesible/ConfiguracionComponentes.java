package ec.campus.accesible;

import ec.campus.accesible.aplicacion.GestorIncidencias;
import ec.campus.accesible.aplicacion.IGestorIncidencias;
import ec.campus.accesible.aplicacion.IIncidenciaRepository;
import ec.campus.accesible.aplicacion.IMapaRepository;
import ec.campus.accesible.aplicacion.IPlanificadorRutas;
import ec.campus.accesible.aplicacion.PlanificadorRutas;
import ec.campus.accesible.infraestructura.IncidenciaRepositoryEnMemoria;
import ec.campus.accesible.infraestructura.MapaCampusEnMemoria;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

/**
 * Único lugar donde se conectan los componentes. Las capas de aplicación y dominio
 * no conocen Spring; así se pueden probar sin levantar el servidor.
 */
@Configuration
public class ConfiguracionComponentes {

    @Bean
    Clock reloj() {
        return Clock.system(ZoneId.of("America/Guayaquil"));
    }

    @Bean
    IMapaRepository mapaRepository() {
        return new MapaCampusEnMemoria();
    }

    @Bean
    IIncidenciaRepository incidenciaRepository() {
        return new IncidenciaRepositoryEnMemoria();
    }

    @Bean
    IPlanificadorRutas planificadorRutas(IMapaRepository mapa) {
        return new PlanificadorRutas(mapa);
    }

    @Bean
    IGestorIncidencias gestorIncidencias(IMapaRepository mapa, IIncidenciaRepository incidencias, Clock reloj) {
        return new GestorIncidencias(mapa, incidencias, reloj);
    }
}
