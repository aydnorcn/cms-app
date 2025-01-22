package com.aydnorcn.mis_app.service;

import com.aydnorcn.mis_app.dto.role.CreateRoleRequestDto;
import com.aydnorcn.mis_app.entity.Role;
import com.aydnorcn.mis_app.exception.AlreadyExistsException;
import com.aydnorcn.mis_app.exception.ResourceNotFoundException;
import com.aydnorcn.mis_app.repository.RoleRepository;
import com.aydnorcn.mis_app.utils.MessageConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private static final String ROLE_PREFIX = "ROLE_";

    public Role getRoleById(String roleId) {
        return roleRepository.findById(roleId).orElseThrow(() -> new ResourceNotFoundException(MessageConstants.ROLE_NOT_FOUND));
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleByName(String name) {
        return roleRepository.findByName(formatRoleName(name)).orElseThrow(() -> new ResourceNotFoundException(MessageConstants.ROLE_NOT_FOUND));
    }

    public Role createRole(CreateRoleRequestDto dto) {
        validateRoleNameDoesNotExist(dto);

        Role role = new Role(formatRoleName(dto.getName()));

        return roleRepository.save(role);
    }

    public Role updateRole(String roleId, CreateRoleRequestDto dto) {
        validateRoleNameDoesNotExist(dto);

        Role updateRole = getRoleById(roleId);

        updateRole.setName(formatRoleName(dto.getName()));


        return roleRepository.save(updateRole);
    }

    public void deleteRole(String roleId) {
        Role role = getRoleById(roleId);
        roleRepository.delete(role);
    }

    private String formatRoleName(String name) {
        return ROLE_PREFIX + name.toUpperCase(Locale.ENGLISH);
    }

    private void validateRoleNameDoesNotExist(CreateRoleRequestDto dto) {
        if (roleRepository.existsByName(formatRoleName(dto.getName()))) {
            throw new AlreadyExistsException(MessageConstants.ROLE_ALREADY_EXISTS);
        }
    }
}
