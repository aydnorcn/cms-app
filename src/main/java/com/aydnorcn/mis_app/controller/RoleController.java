package com.aydnorcn.mis_app.controller;

import com.aydnorcn.mis_app.dto.role.CreateRoleRequestDto;
import com.aydnorcn.mis_app.entity.Role;
import com.aydnorcn.mis_app.exception.ErrorMessage;
import com.aydnorcn.mis_app.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "Role Controller")
public class RoleController {

    private final RoleService roleService;

    @Operation(
            summary = "Retrieve role by id"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Role found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Role.class))),
                    @ApiResponse(responseCode = "404", description = "Role not found | If given id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Not have permission to access | If user not admin or moderator",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @GetMapping("/{roleId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Role> getRoleById(@PathVariable String roleId) {
        return ResponseEntity.ok(roleService.getRoleById(roleId));
    }


    @Operation(
            summary = "Retrieve role by name"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Role found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Role.class))),
                    @ApiResponse(responseCode = "404", description = "Role not found | If given name is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Not have permission to access | If user not admin or moderator",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Role> getRoleByName(@RequestParam String name) {
        return ResponseEntity.ok(roleService.getRoleByName(name));
    }


    @Operation(
            summary = "Create a new role"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Role created",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Role.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request | If role name is empty or not enough length",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Not have permission to access | If user not admin",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "409", description = "Conflict | If role name already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Role> createRole(@Validated @RequestBody CreateRoleRequestDto role) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.createRole(role));
    }

    @Operation(
            summary = "Update role name by id"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Role updated",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Role.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request | If role name is empty or not enough length",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Not have permission to access | If user not admin",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Role not found | If given id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "409", description = "Conflict | If role name already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PutMapping("/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Role> updateRole(@PathVariable String roleId, @Validated @RequestBody CreateRoleRequestDto role) {
        return ResponseEntity.ok(roleService.updateRole(roleId, role));
    }

    @Operation(
            summary = "Delete role by giving id"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Role deleted"),
                    @ApiResponse(responseCode = "403", description = "Not have permission to access | If user not admin",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Role not found | If given id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @DeleteMapping("/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRole(@PathVariable String roleId) {
        roleService.deleteRole(roleId);
        return ResponseEntity.noContent().build();
    }
}