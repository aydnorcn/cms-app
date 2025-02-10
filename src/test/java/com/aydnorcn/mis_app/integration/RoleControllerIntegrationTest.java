package com.aydnorcn.mis_app.integration;

import com.aydnorcn.mis_app.dto.role.CreateRoleRequestDto;
import com.aydnorcn.mis_app.entity.Role;
import com.aydnorcn.mis_app.integration.support.RoleControllerIntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
class RoleControllerIntegrationTest extends RoleControllerIntegrationTestSupport {

    private final String API_URL = "/api/roles";

    @Test
    void getRoleById_ReturnRole_WhenRoleExists() throws Exception {
        Role role = roles.get(0);

        mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/" + role.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(role.getId()))
                .andExpect(jsonPath("$.data.name").value(role.getName()));
    }

    @Test
    void getRoleById_ReturnForbidden_WhenUserIsNotAuthenticated() throws Exception {
        Role role = roles.get(0);

        mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/" + role.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getRoleById_ReturnNotFound_WhenRoleDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/not-exist-id")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getRoleByName_ReturnRole_WhenRoleExists() throws Exception {
        String roleName = "user";

        mockMvc.perform(MockMvcRequestBuilders.get(API_URL)
                        .param("name", roleName)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("ROLE_" + roleName.toUpperCase()));
    }

    @Test
    void getRoleByName_ReturnNotFound_WhenRoleDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(API_URL)
                        .param("name", "not-exist-role")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isNotFound());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void createRole_ReturnRole_WhenNameIsNotExists() throws Exception {
        CreateRoleRequestDto request = new CreateRoleRequestDto();
        request.setName("new-role");

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("ROLE_" + request.getName().toUpperCase()));
    }

    @Test
    void createRole_ReturnConflict_WhenNameExists() throws Exception {
        CreateRoleRequestDto request = new CreateRoleRequestDto();
        request.setName("user");

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isConflict());
    }

    @Test
    void createRole_ReturnBadRequest_WhenNameIsNull() throws Exception {
        CreateRoleRequestDto request = new CreateRoleRequestDto();
        request.setName(null);

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isBadRequest());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void updateRole_ReturnRole_WhenNameIsNotExists() throws Exception {
        Role role = roles.get(0);
        CreateRoleRequestDto request = new CreateRoleRequestDto();
        request.setName("new-role");

        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/" + role.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("ROLE_" + request.getName().toUpperCase()));
    }

    @Test
    void updateRole_ReturnConflict_WhenNameExists() throws Exception {
        Role role = roles.get(0);
        CreateRoleRequestDto request = new CreateRoleRequestDto();
        request.setName(roles.get(1).getName().substring(5));

        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/" + role.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isConflict());
    }

    @Test
    void updateRole_ReturnBadRequest_WhenNameIsNull() throws Exception {
        Role role = roles.get(0);
        CreateRoleRequestDto request = new CreateRoleRequestDto();
        request.setName(null);

        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/" + role.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isBadRequest());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void deleteRole_ReturnNoContent_WhenRoleExists() throws Exception {
        Role role = new Role();
        role.setName("NEW_ROLE");
        roleRepository.save(role);

        mockMvc.perform(MockMvcRequestBuilders.delete(API_URL + "/" + role.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteRole_ReturnNotFound_WhenRoleDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(API_URL + "/not-exist-id")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isNotFound());
    }
}