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
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = "com.github.nramc.dev.journey.api", importOptions = {ImportOption.DoNotIncludeTests.class})
@SuppressWarnings("unused")
public class JourneyApplicationArchitectureTest {

    @ArchTest
    public static final ArchRule ruleLimitDomainsDependency = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage("..usecase..", "..config..", "..services", "..gateways..", "..repository..", "..web..");

    @ArchTest
    public static final ArchRule ruleLimitUseCasesDependant = classes()
            .that().resideInAPackage("..usecase..")
            .should().onlyBeAccessed().byAnyPackage("..resources..", "..usecase..", "..config..");

    @ArchTest
    public static final ArchRule ruleLimitGatewaysDependant = classes()
            .that().resideInAPackage("..gateway..")
            .and().haveSimpleNameEndingWith("Gateway")
            .should().onlyBeAccessed().byAnyPackage("..config..", "..usecase..",
                    "..services..", "..gateway..", "..resources..");

    @ArchTest
    public static final ArchRule ruleManageRepositoryDependant = classes()
            .that().resideInAPackage("..repository..")
            .and().haveSimpleNameEndingWith("Repository")
            .should().onlyBeAccessed().byAnyPackage("..repository..", "..usecase..",
                    "..resources.rest.users.find..");

    @ArchTest
    public static final ArchRule ruleLimitRepositoryEntityDependant = classes()
            .that().resideInAPackage("..repository..")
            .and().haveSimpleNameEndingWith("Entity")
            .should().onlyBeAccessed().byAnyPackage("..repository..", "..migration..",
                    "..usecase..", "..resources.rest.journeys..", "..core.journey.security..");

    @ArchTest
    public static final ArchRule ruleResourcesNamingConvention = classes()
            .that().areAnnotatedWith(RestController.class).or().areAnnotatedWith(Controller.class)
            .should().resideInAPackage("..web.resources..").andShould().haveSimpleNameEndingWith("Resource");


    @ArchTest
    public static final ArchRule ruleIsolateUtilities = classes()
            .that().resideInAPackage("..utils..")
            .should().onlyDependOnClassesThat().
            resideOutsideOfPackages("..web..", "..gateway..", "..service..", "..repository..", "..usecase..");

    @ArchTest
    public static final ArchRule ruleCyclicDependencyPrevention = slices()
            .matching("com.github.nramc.dev.journey.api.core.(*)..").should().beFreeOfCycles();

    @ArchTest
    public static final ArchRule ruleLimitStereotypeAnnotationsUsage = classes()
            .that().doNotImplement(UserDetailsManager.class)
            .should().notBeAnnotatedWith(Service.class)
            .andShould().notBeAnnotatedWith(Component.class)
            .andShould().notBeAnnotatedWith(Repository.class);

    @ArchTest
    public static final ArchRule rulePreventFieldLevelInjection = noFields().should().beAnnotatedWith(Autowired.class);

    @ArchTest
    public static final ArchRule ruleEnforceDependencyManagementWithConfig = methods()
            .that().areAnnotatedWith(Bean.class)
            .should().beDeclaredInClassesThat().resideInAPackage("..config..")
            .andShould().beDeclaredInClassesThat().areAnnotatedWith(Configuration.class);

}
