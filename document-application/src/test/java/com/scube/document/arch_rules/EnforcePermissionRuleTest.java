package com.scube.document.arch_rules;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.lang.*;
import jakarta.annotation.security.RolesAllowed;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.scube.lib.misc.archunit.ClassValidationUtils.getClasses;
import static com.tngtech.archunit.lang.ConditionEvent.createMessage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.all;

class EnforcePermissionRuleTest {

    /**
     * classes that are annotated with RestController should have a method that is annotated with PreAuthorize or Secured or PostAuthorize or RolesAllowed
     * if the method is of type GET, POST, PUT, DELETE it means it is an endpoint
     *
     * <p>the excludedClasses are classes that don't require permission annotation</p>
     */
    @Test
    void testThatEachRestControllerHasPermissionAnnotation() {
        // if a controller is meant to be public you can add it to the excluded classes list
        var excludedClasses = List.of();

        ClassesTransformer<JavaMethod> restControllerEndpoints = new AbstractClassesTransformer<>("RestController endpoints") {
            @Override
            public Iterable<JavaMethod> doTransform(JavaClasses classes) {
                Set<JavaMethod> result = new HashSet<>();
                //check that the class is a rest controller
                var restControllerClasses = classes.stream()
                        .filter(javaClass -> javaClass.isAnnotatedWith(RestController.class))
                        .filter(javaClass -> !excludedClasses.contains(javaClass.reflect()))
                        .toList();
                for (JavaClass javaClass : restControllerClasses) {
                    var annotations = List.of(GetMapping.class, PostMapping.class, PutMapping.class, DeleteMapping.class, PatchMapping.class, RequestMapping.class);
                    javaClass.getMethods().stream()
                            .filter(method -> annotations.stream().anyMatch(method::isAnnotatedWith))
                            .forEach(result::add);
                }
                return result;
            }

        };

        ArchCondition<JavaMethod> havePermissionsEndpoints = new ArchCondition<>("have any of the permissions annotation") {
            @Override
            public void check(JavaMethod method, ConditionEvents events) {
                // check that an endpoint has any of the permission annotation
                var annotations = List.of(RolesAllowed.class, PreAuthorize.class, Secured.class, PostAuthorize.class);
                if (annotations.stream().noneMatch(method::isAnnotatedWith)) {
                    events.add(SimpleConditionEvent.violated(method, createMessage(method, "should have any of the permission annotation")));
                }
            }
        };

        all(restControllerEndpoints).should(havePermissionsEndpoints).check(getClasses());

    }
}
