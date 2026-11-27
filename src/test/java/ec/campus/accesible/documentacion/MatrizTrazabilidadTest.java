package ec.campus.accesible.documentacion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Lee docs/matriz-trazabilidad.md y comprueba que cada fila apunte a cosas que existen:
 * un componente del modelo C4, un método de producción y un método de prueba.
 */
class MatrizTrazabilidadTest {

    private static final Path MATRIZ = Path.of("docs/matriz-trazabilidad.md");
    private static final Pattern CODIGO = Pattern.compile("`([^`]+)`");
    private static final List<String> PAQUETES = List.of(
            "ec.campus.accesible.dominio", "ec.campus.accesible.aplicacion",
            "ec.campus.accesible.api", "ec.campus.accesible.infraestructura");

    record Fila(String historia, String componente, String implementacion, String prueba) {
    }

    private static List<Fila> filas() throws IOException {
        List<Fila> filas = new ArrayList<>();
        for (String linea : Files.readAllLines(MATRIZ)) {
            if (!linea.startsWith("| HU-")) {
                continue;
            }
            String[] celdas = linea.split("\\|");
            filas.add(new Fila(celdas[1].trim(), codigo(celdas[2]), codigo(celdas[3]), codigo(celdas[4])));
        }
        return filas;
    }

    private static String codigo(String celda) {
        Matcher m = CODIGO.matcher(celda);
        return m.find() ? m.group(1) : "";
    }

    private static Optional<Class<?>> clase(String nombreSimple) {
        for (String paquete : PAQUETES) {
            try {
                return Optional.of(Class.forName(paquete + "." + nombreSimple));
            } catch (ClassNotFoundException ignorada) {
                // se prueba el siguiente paquete
            }
        }
        return Optional.empty();
    }

    private static boolean existeMetodo(String claseYMetodo) {
        int punto = claseYMetodo.lastIndexOf('.');
        if (punto < 0) {
            return false;
        }
        String metodo = claseYMetodo.substring(punto + 1);
        return clase(claseYMetodo.substring(0, punto))
                .map(c -> Arrays.stream(c.getDeclaredMethods()).map(Method::getName).anyMatch(metodo::equals))
                .orElse(false);
    }

    @Test
    @DisplayName("La matriz tiene al menos 5 trazas, como pide la práctica")
    void cincoTrazas() throws IOException {
        assertThat(filas()).hasSizeGreaterThanOrEqualTo(5);
    }

    @Test
    @DisplayName("Cada fila apunta a un componente C4, un método y una prueba que existen")
    void trazasVigentes() throws IOException {
        Set<String> componentes = ModeloSincronizadoTest.componentesDelModelo().keySet();

        for (Fila fila : filas()) {
            assertThat(componentes)
                    .as("%s: el componente «%s» no está en workspace.dsl", fila.historia(), fila.componente())
                    .contains(fila.componente());
            assertThat(existeMetodo(fila.implementacion()))
                    .as("%s: no existe el método de producción «%s»", fila.historia(), fila.implementacion())
                    .isTrue();
            assertThat(existeMetodo(fila.prueba()))
                    .as("%s: no existe la prueba «%s»", fila.historia(), fila.prueba())
                    .isTrue();
        }
    }
}
