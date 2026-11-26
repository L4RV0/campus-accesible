package ec.campus.accesible;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Las flechas del diagrama de componentes, comprobadas en cada build.
 * Si alguien agrega una dependencia que el diagrama no tiene, esta prueba falla.
 */
@AnalyzeClasses(packages = "ec.campus.accesible", importOptions = ImportOption.DoNotIncludeTests.class)
class ArquitecturaTest {

    @ArchTest
    static final ArchRule dominioNoDependeDeNadie = noClasses()
            .that().resideInAPackage("..dominio..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..aplicacion..", "..infraestructura..", "..api..", "org.springframework..");

    @ArchTest
    static final ArchRule aplicacionNoConoceInfraestructuraNiApi = noClasses()
            .that().resideInAPackage("..aplicacion..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..infraestructura..", "..api..", "org.springframework..");

    @ArchTest
    static final ArchRule apiNoUsaInfraestructuraDirectamente = noClasses()
            .that().resideInAPackage("..api..")
            .should().dependOnClassesThat()
            .resideInAPackage("..infraestructura..");
}
