package com.aydnorcn.mis_app.integration;

import com.aydnorcn.mis_app.dto.assignment.CreateAssignmentRequest;
import com.aydnorcn.mis_app.dto.assignment.PatchAssignmentRequest;
import com.aydnorcn.mis_app.integration.support.AssignmentControllerIntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
class AssignmentControllerIntegrationTest extends AssignmentControllerIntegrationTestSupport {

    private final String API_URL = "/api/assignments";

    @Test
    void getAssignmentById_ReturnsAssignment_WhenAssignmentExists() throws Exception{
        String id = assignments.get(0).getId();

        mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(id));
    }

    @Test
    void getAssignmentById_ReturnsNotFound_WhenAssignmentDoesNotExist() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAssignments_ReturnsAssignments_WhenRequestIsValid() throws Exception{
        int count = assignments.stream().filter(x -> x.getPriority() >= 3).toList().size();

        mockMvc.perform(MockMvcRequestBuilders.get(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("min-priority", "3")
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(count));
    }

    @Test
    void getAssignments_ReturnsUnauthorized_WhenUserIsNotAuthorized() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get(API_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAssignments_ReturnsValues_WhenEmptyParams() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(assignments.size()));
    }

    @Test
    void createAssignment_ReturnsCreated_WhenValidRequest() throws Exception{
        CreateAssignmentRequest request = new CreateAssignmentRequest();
        request.setAssignedTo(List.of(user.getId()));
        request.setEventId(events.get(0).getId());
        request.setTitle("Test Assignment");
        request.setContent("Test Assignment Content");
        request.setPriority(3);

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isCreated());
    }

    @Test
    void createAssignment_ReturnsBadRequest_WhenInvalidRequest() throws Exception{
        CreateAssignmentRequest request = new CreateAssignmentRequest();
        request.setAssignedTo(List.of(user.getId()));
        request.setEventId(events.get(0).getId());
        request.setTitle("Test Assignment");
        request.setContent("Test Assignment Content");
        request.setPriority(6);

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createAssignment_ReturnsForbidden_WhenUserIsNotPermitted() throws Exception{
        CreateAssignmentRequest request = new CreateAssignmentRequest();
        request.setAssignedTo(List.of(user.getId()));
        request.setEventId(events.get(0).getId());
        request.setTitle("Test Assignment");
        request.setContent("Test Assignment Content");
        request.setPriority(3);

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateAssignment_ReturnsAssignment_WhenIdAndRequestAreValid() throws Exception{
        CreateAssignmentRequest request = new CreateAssignmentRequest();
        request.setAssignedTo(List.of(user.getId()));
        request.setEventId(events.get(0).getId());
        request.setTitle("Test Assignment");
        request.setContent("Test Assignment Content");
        request.setPriority(3);

        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/" + assignments.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isOk());
    }

    @Test
    void updateAssignment_ReturnsNotFound_WhenIdIsInvalid() throws Exception{
        CreateAssignmentRequest request = new CreateAssignmentRequest();
        request.setAssignedTo(List.of(user.getId()));
        request.setEventId(events.get(0).getId());
        request.setTitle("Test Assignment");
        request.setContent("Test Assignment Content");
        request.setPriority(3);

        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateAssignment_ReturnsBadRequest_WhenRequestIsInvalid() throws Exception{
        CreateAssignmentRequest request = new CreateAssignmentRequest();
        request.setAssignedTo(List.of(user.getId()));
        request.setEventId(events.get(0).getId());
        request.setTitle("");
        request.setContent("Test Assignment Content");
        request.setPriority(10);

        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateAssignment_ReturnsForbidden_WhenUserIsNotPermitted() throws Exception{
        CreateAssignmentRequest request = new CreateAssignmentRequest();
        request.setAssignedTo(List.of(user.getId()));
        request.setEventId(events.get(0).getId());
        request.setTitle("Test Assignment");
        request.setContent("Test Assignment Content");
        request.setPriority(3);

        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/" + assignments.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isForbidden());
    }

    @Test
    void patchAssignment_ReturnsAssignment_WhenIdAndRequestAreValid() throws Exception{
        PatchAssignmentRequest request = new PatchAssignmentRequest();
        request.setTitle("New Title");
        request.setContent("New Content");

        mockMvc.perform(MockMvcRequestBuilders.patch(API_URL + "/" + assignments.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isOk());
    }

    @Test
    void patchAssignment_ReturnsNotFound_WhenIdIsInvalid() throws Exception{
        PatchAssignmentRequest request = new PatchAssignmentRequest();
        request.setTitle("New Title");
        request.setContent("New Content");

        mockMvc.perform(MockMvcRequestBuilders.patch(API_URL + "/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchAssignment_ReturnsForbidden_WhenUserIsNotPermitted() throws Exception{
        PatchAssignmentRequest request = new PatchAssignmentRequest();
        request.setTitle("New Title");
        request.setContent("New Content");

        mockMvc.perform(MockMvcRequestBuilders.patch(API_URL + "/" + assignments.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteAssignment_ReturnsNoContent_WhenIdIsValid() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.delete(API_URL + "/" + assignments.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteAssignment_ReturnsNotFound_WhenIdIsInvalid() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.delete(API_URL + "/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteAssignment_ReturnsForbidden_WhenUserIsNotPermitted() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.delete(API_URL + "/" + assignments.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isForbidden());
    }
}