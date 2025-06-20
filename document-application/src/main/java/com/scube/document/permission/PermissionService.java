package com.scube.document.permission;

import com.scube.auth.controllers.gen_dto.CompositeRoleRequest;
import com.scube.auth.library.enabled_true.keycloak.services.KeycloakRealmService;
import com.scube.client.auth.generated.AuthServiceConnection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionService {
    private final AuthServiceConnection auth;
    private final KeycloakRealmService keycloakRealmService;

    @Value("${spring.application.name}")
    private String serviceName;

    public void addRoleToRealm(String realmName) {
        log.info("Seeding permissions");

        Set<String> permissions = getAllPermissions();

        addRoleToRealm(realmName, permissions);

        log.info("Seeding permissions");
    }

    public void addRoleToRealm() {
        log.info("Seeding permissions");

        Set<String> permissions = getAllPermissions();
        List<String> realms = auth.publicRealm().getAllRealmNames();

        for (var realmName : realms) {
            addRoleToRealm(realmName, permissions);
        }

        log.info("Seeding permissions");
    }

    public void addRoleToRealm(String realmName, Set<String> permissions) {
        var applicationName = convertCamelToDashCase(serviceName);
        var adminCompositeRoleName = applicationName + "-admin-roles";
        var meCompositeRoleName = applicationName + "-me-roles";

        var adminPermissions = permissions.stream().filter(p -> !p.contains("-me-")).toList();
        var mePermissions = permissions.stream().filter(p -> p.contains("-me-")).toList();

        keycloakRealmService.addCompositeRoles(realmName, adminCompositeRoleName, adminPermissions);
        keycloakRealmService.addCompositeRoles(realmName, meCompositeRoleName, mePermissions);
    }

    private static Set<String> getAllPermissions() {
        Set<String> permissions = getPermissions(Permissions.class);
        permissions.addAll(getPermissions(com.scube.scheduling.lib.Permissions.class));
        return permissions;
    }

    private static Set<String> getPermissions(Class<?> clazz) {
        Set<String> constantValues = new HashSet<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()) && Modifier.isPublic(field.getModifiers())) {
                try {
                    Object value = field.get(null);
                    constantValues.add(value.toString());
                } catch (IllegalAccessException e) {
                    log.error("Error getting constant value", e);
                }
            }
        }
        // Get constants from nested classes
        Class<?>[] nestedClasses = clazz.getDeclaredClasses();
        for (Class<?> nestedClass : nestedClasses) {
            constantValues.addAll(getPermissions(nestedClass));
        }
        return constantValues;
    }

    private static String convertCamelToDashCase(@NonNull String text) {
        text = text.substring(0, 1).toLowerCase() + text.substring(1);
        return text.replace("Service", "-service").toLowerCase();
    }
}