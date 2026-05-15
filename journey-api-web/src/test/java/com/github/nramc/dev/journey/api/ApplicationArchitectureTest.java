package com.github.nramc.dev.journey.api;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;

/**
 * Lightweight ArchUnit rules that complement Spring Modulith boundary verification.
 *
 * <p>Module boundary rules (cyclic deps, layer access, etc.) are enforced by
 * {@link ApplicationModulesTest} via {@code ApplicationModules.verify()}.
 * This class only enforces coding conventions that Modulith does not cover.
 */
@AnalyzeClasses(packages = "com.github.nramc.dev.journey.api",
        importOptions = {ImportOption.DoNotIncludeTests.class})
@SuppressWarnings({"unused", "java:S1192"})
final class ApplicationArchitectureTest {

    /**
     * All Spring MVC/REST controllers must:
     * <ul>
     *   <li>reside in a {@code .web.} package within their module</li>
     *   <li>have a class name ending with {@code Resource}</li>
     * </ul>
     */
    @ArchTest
    public static final ArchRule ruleResourcesNamingConvention = classes()
            .that().areAnnotatedWith(RestController.class)
            .or().areAnnotatedWith(Controller.class)
            .should().resideInAPackage("..web..")
            .andShould().haveSimpleNameEndingWith("Resource");

    /**
     * No Spring stereotype annotations (except on {@link UserDetailsManager} impls).
     * All beans must be wired via {@code @Bean} methods in {@code @Configuration} classes.
     */
    @ArchTest
    public static final ArchRule ruleNoStereotypeAnnotations = classes()
            .that().doNotImplement(UserDetailsManager.class)
            .should().notBeAnnotatedWith(Service.class)
            .andShould().notBeAnnotatedWith(Component.class)
            .andShould().notBeAnnotatedWith(Repository.class);

    /**
     * Constructor injection only — no field-level {@code @Autowired}.
     */
    @ArchTest
    public static final ArchRule ruleNoFieldInjection = noFields()
            .should().beAnnotatedWith(Autowired.class);

    /**
     * Every {@code @Bean} method must be declared in a {@code @Configuration}-annotated
     * class that resides in a {@code .config.} package.
     */
    @ArchTest
    public static final ArchRule ruleBeansInConfigClasses = methods()
            .that().areAnnotatedWith(Bean.class)
            .should().beDeclaredInClassesThat().resideInAnyPackage("..config..", "..infrastructure..")
            .andShould().beDeclaredInClassesThat().areAnnotatedWith(Configuration.class);

    private ApplicationArchitectureTest() {
    }
}
