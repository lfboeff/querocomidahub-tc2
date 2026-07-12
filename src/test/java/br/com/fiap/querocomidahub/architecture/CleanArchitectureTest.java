package br.com.fiap.querocomidahub.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@DisplayName("ArchUnit")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CleanArchitectureTest {

    private static JavaClasses classes;

    @BeforeAll
    static void importClasses() {
        classes = new ClassFileImporter().importPackages("br.com.fiap.querocomidahub");
    }

    @Test
    void domain_does_not_depend_on_application_or_infrastructure() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..application..", "..infrastructure..");
        rule.check(classes);
    }

    @Test
    void application_does_not_depend_on_infrastructure() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..application..")
                .should().dependOnClassesThat()
                .resideInAPackage("..infrastructure..");
        rule.check(classes);
    }

    @Test
    void domain_must_not_import_spring_jakarta_web_or_jackson() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage(
                        "org.springframework..",
                        "jakarta.servlet..",
                        "jakarta.persistence..",
                        "com.fasterxml.jackson..");
        rule.check(classes);
    }

    @Test
    void application_must_not_import_spring_web_jackson_or_servlets() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..application..")
                .should().dependOnClassesThat()
                .resideInAnyPackage(
                        "org.springframework.web..",
                        "org.springframework.stereotype..",
                        "jakarta.servlet..",
                        "com.fasterxml.jackson..");
        rule.check(classes);
    }

    @Test
    void domain_gateways_must_be_interfaces() {
        ArchRule rule = classes()
                .that().resideInAPackage("..domain.gateway..")
                .should().beInterfaces();
        rule.check(classes);
    }

    @Test
    void application_controllers_must_not_be_annotated_with_spring_stereotypes() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..application.controller..")
                .should().beAnnotatedWith("org.springframework.stereotype.Component")
                .orShould().beAnnotatedWith(RestController.class)
                .orShould().beAnnotatedWith("org.springframework.stereotype.Service");
        rule.check(classes);
    }

    @Test
    void rest_controller_must_only_reside_in_infrastructure_web() {
        ArchRule rule = classes()
                .that().areAnnotatedWith(RestController.class)
                .should().resideInAPackage("..infrastructure.web..");
        rule.check(classes);
    }

    @Test
    void swagger_annotations_must_only_reside_in_infrastructure_web() {
        ArchRule rule = noClasses()
                .that().resideOutsideOfPackage("..infrastructure.web..")
                .should().dependOnClassesThat()
                .resideInAPackage("io.swagger.v3.oas.annotations..");
        rule.check(classes);
    }

    @Test
    void use_cases_must_reside_in_application_usecase() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("UseCase")
                .should().resideInAPackage("..application.usecase..");
        rule.check(classes);
    }

    @Test
    void jdbc_gateways_must_reside_in_infrastructure_gateway() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("JdbcGateway")
                .should().resideInAPackage("..infrastructure.gateway..");
        rule.check(classes);
    }

    // -----------------------------------------------------------------------
    // Spring stereotype annotations must only reside in infrastructure
    // -----------------------------------------------------------------------

    @Test
    void spring_configuration_must_only_reside_in_infrastructure() {
        ArchRule rule = classes()
                .that().areAnnotatedWith(Configuration.class)
                .and().areNotAnnotatedWith("org.springframework.boot.autoconfigure.SpringBootApplication")
                .should().resideInAPackage("..infrastructure..");
        rule.check(classes);
    }

    @Test
    void spring_repository_must_only_reside_in_infrastructure() {
        ArchRule rule = classes()
                .that().areAnnotatedWith(Repository.class)
                .should().resideInAPackage("..infrastructure..");
        rule.check(classes);
    }

    @Test
    void controller_advice_must_only_reside_in_infrastructure() {
        ArchRule rule = classes()
                .that().areAnnotatedWith(RestControllerAdvice.class)
                .or().areAnnotatedWith(ControllerAdvice.class)
                .should().resideInAPackage("..infrastructure..");
        rule.check(classes);
    }

    // -----------------------------------------------------------------------
    // Naming conventions and correct layer placement
    // -----------------------------------------------------------------------

    @Test
    void dto_mappers_must_reside_in_application_mapper() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("DTOMapper")
                .should().resideInAPackage("..application.mapper..");
        rule.check(classes);
    }

    @Test
    void json_mappers_must_reside_in_infrastructure_web_mapper() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("JSONMapper")
                .should().resideInAPackage("..infrastructure.web.mapper..");
        rule.check(classes);
    }

    @Test
    void input_and_output_dtos_must_reside_in_application_dto() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("InputDTO")
                .or().haveSimpleNameEndingWith("OutputDTO")
                .should().resideInAPackage("..application.dto..");
        rule.check(classes);
    }

    @Test
    void request_json_must_reside_in_infrastructure_web() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("RequestJson")
                .should().resideInAPackage("..infrastructure.web..");
        rule.check(classes);
    }

    @Test
    void response_json_must_reside_in_infrastructure_web() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("ResponseJson")
                .should().resideInAPackage("..infrastructure.web..");
        rule.check(classes);
    }

    @Test
    void config_classes_must_reside_in_infrastructure_config() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Config")
                .and().areTopLevelClasses()
                .should().resideInAPackage("..infrastructure.config..");
        rule.check(classes);
    }

    // -----------------------------------------------------------------------
    // Use cases must be plain Java objects (no Spring stereotypes)
    // -----------------------------------------------------------------------

    @Test
    void use_cases_must_not_have_spring_stereotype_annotations() {
        ArchRule rule = noClasses()
                .that().haveSimpleNameEndingWith("UseCase")
                .should().beAnnotatedWith("org.springframework.stereotype.Component")
                .orShould().beAnnotatedWith("org.springframework.stereotype.Service")
                .orShould().beAnnotatedWith(RestController.class)
                .orShould().beAnnotatedWith(Repository.class);
        rule.check(classes);
    }
}
