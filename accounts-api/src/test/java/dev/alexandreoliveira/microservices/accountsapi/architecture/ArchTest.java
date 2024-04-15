package dev.alexandreoliveira.microservices.accountsapi.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ArchTest {

    @Test
    @Order(1)
    void checkControllers() {
        ImportOption ignoreTestsFiles = location -> !location.contains("/test/");

        JavaClasses importPackages = new ClassFileImporter()
                .withImportOption(ignoreTestsFiles)
                .importPackages("dev.alexandreoliveira.microservices.accountsapi.controllers");

        ArchRule myRule = ArchRuleDefinition.classes()
                .that()
                .areAnnotatedWith(RestController.class)
                .should()
                .haveSimpleNameEndingWith("Controller")
                .andShould()
                .haveOnlyFinalFields()
                .andShould()
                .accessClassesThat()
                .haveSimpleNameEndingWith("Service")
                .andShould()
                .accessClassesThat()
                .areInterfaces();

        myRule.check(importPackages);
    }

    @Test
    @Order(2)
    void checkServices() {
        ImportOption ignoreTestsFiles = location -> !location.contains("/test/");

        JavaClasses importPackages = new ClassFileImporter()
                .withImportOption(ignoreTestsFiles)
                .importPackages("dev.alexandreoliveira.microservices.accountsapi.services");

        ArchRule myRule = ArchRuleDefinition.classes()
                .that()
                .areAnnotatedWith(Service.class)
                .should()
                .haveSimpleNameEndingWith("Service")
                .andShould()
                .accessClassesThat()
                .resideInAnyPackage("..services..", "..repositories..", "..mappers..");

        myRule.check(importPackages);
    }

    @Test
    @Order(3)
    void checkRepositories() {
        ImportOption ignoreTestsFiles = location -> !location.contains("/test/");

        JavaClasses importPackages = new ClassFileImporter()
                .withImportOption(ignoreTestsFiles)
                .importPackages("dev.alexandreoliveira.microservices.accountsapi.database.repositories");

        ArchRule myRule = ArchRuleDefinition.classes()
                .that()
                .areAnnotatedWith(Repository.class)
                .should()
                .beInterfaces()
                .andShould()
                .dependOnClassesThat()
                .areAssignableFrom(JpaRepository.class);

        myRule.check(importPackages);
    }
}
