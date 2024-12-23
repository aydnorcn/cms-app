package com.aydnorcn.mis_app.controller;

import com.aydnorcn.mis_app.dto.role.CreateRoleRequestDto;
import com.aydnorcn.mis_app.entity.Role;
import com.aydnorcn.mis_app.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/{roleId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MOD')")
    public ResponseEntity<Role> getRoleById(@PathVariable String roleId) {
        return ResponseEntity.ok(roleService.getRoleById(roleId));
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MOD')")
    public ResponseEntity<Role> getRoleByName(@RequestParam String name) {
        return ResponseEntity.ok(roleService.getRoleByName(name));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Role> createRole(@RequestBody CreateRoleRequestDto role) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.createRole(role));
    }

    @PutMapping("/{roleId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Role> updateRole(@PathVariable String roleId, @RequestBody CreateRoleRequestDto role) {
        return ResponseEntity.ok(roleService.updateRole(roleId, role));
    }

    @DeleteMapping("/{roleId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteRole(@PathVariable String roleId) {
        roleService.deleteRole(roleId);
        return ResponseEntity.noContent().build();
    }
}