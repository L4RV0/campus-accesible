package ec.campus.accesible.documentacion;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Compara los componentes del modelo C4 (docs/arquitectura/workspace.dsl) con las clases reales.
 * Falla si hay un componente dibujado que no existe en el código, o una clase que nadie dibujó.
 */
class ModeloSincronizadoTest {

    static final Path MODELO = Path.of("docs/arquitectura/workspace.dsl");
    private static final Pattern COMPONENTE =
            Pattern.compile("component\\s+\"([^\"]+)\"\\s+\"[^\"]*\"\\s+\"(api|aplicacion|infraestructura)\"");
    private static final String BASE = "ec.campus.accesible.";

    static Map<String, String> componentesDelModelo() throws IOException {
        Map<String, String> componentes = new TreeMap<>();
        Matcher m = COMPONENTE.matcher(Files.readString(MODELO));
        while (m.find()) {
            componentes.put(m.group(1), m.group(2));
        }
        return componentes;
    }

    private static Map<String, String> clasesDelCodigo() {
        Map<String, String> clases = new TreeMap<>();
        new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages(BASE + "api", BASE + "aplicacion", BASE + "infraestructura")
                .stream()
                .filter(JavaClass::isTopLevelClass)
                .filter(c -> !c.isInterface())
                // Las excepciones no son componentes C4: son parte del contrato de error.
                .filter(c -> !c.isAssignableTo(Throwable.class))
                .forEach(c -> clases.put(c.getSimpleName(), c.getPackageName().substring(BASE.length())));
        return clases;
    }

    @Test
    @DisplayName("Cada componente del C4 es una clase real, en el paquete que dice el modelo")
    void modeloSinComponentesFantasma() throws IOException {
        Map<String, String> modelo = componentesDelModelo();

        assertThat(modelo).as("componentes leídos de workspace.dsl").isNotEmpty();
        assertThat(clasesDelCodigo())
                .as("Hay componentes en workspace.dsl que no existen en el código")
                .containsAllEntriesOf(modelo);
    }

    @Test
    @DisplayName("Cada clase de api, aplicacion e infraestructura aparece en el C4")
    void codigoSinClasesSinDibujar() throws IOException {
        assertThat(componentesDelModelo())
                .as("Hay clases nuevas que no están en workspace.dsl: actualiza el diagrama de componentes")
                .containsAllEntriesOf(clasesDelCodigo());
    }
}
