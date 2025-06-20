package com.scube.document.permission;

import com.scube.client.ServiceUrlConstant;
import com.scube.client.annotation.GenerateHttpExchange;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@GenerateHttpExchange(value = ServiceUrlConstant.DOCUMENT_SERVICE)
@Validated
public class PermissionController {
    private final PermissionService permissionService;

    @PostMapping
    @Operation(summary = "Seed role to all realms")
    @RolesAllowed(Permissions.Permission.SEED_ROLES_TO_ALL_REALMS)
    public void seedRolesToAllRealms() {
        permissionService.addRoleToRealm();
    }

    @PostMapping("/{realmName}")
    @Operation(summary = "Seed role to a specific realm")
    @RolesAllowed(Permissions.Permission.SEED_ROLES_BY_REALM)
    public void seedRolesByRealm(@PathVariable @Size(max = 255) String realmName) {
        permissionService.addRoleToRealm(realmName);
    }
}
