package com.github.nramc.dev.journey.api;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;
import org.springframework.modulith.docs.Documenter.DiagramOptions;
import org.springframework.modulith.docs.Documenter.Options;

/**
 * Spring Modulith structural verification and documentation generation.
 *
 * <p>Verifies that all declared {@code @ApplicationModule} boundaries are respected
 * (no illegal cross-module access, no cycles, allowed-dependencies honoured).
 *
 * <p>Also generates PlantUML diagrams under {@code target/spring-modulith-docs/}
 * which can be embedded in project documentation.
 */
class ApplicationModulesTest {

    private static final ApplicationModules MODULES = ApplicationModules.of(JourneyApiApplication.class);

    @Test
    void verifiesModuleStructure() {
        MODULES.verify();
    }

    @Test
    void generatesDocumentation() {
        var documenter = new Documenter(MODULES, Options.defaults())
                .writeModulesAsPlantUml(DiagramOptions.defaults())
                .writeIndividualModulesAsPlantUml(DiagramOptions.defaults())
                .writeModuleCanvases()
                .writeAggregatingDocument();
        Assertions.assertThat(documenter).isNotNull();
    }
}
