package com.aydnorcn.mis_app.integration;

import com.aydnorcn.mis_app.dto.event.CreateEventRequest;
import com.aydnorcn.mis_app.dto.event.PatchEventRequest;
import com.aydnorcn.mis_app.entity.Event;
import com.aydnorcn.mis_app.utils.EventStatus;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
class EventControllerIntegrationTest extends EventControllerIntegrationTestSupport {


    private final String API_URL = "/api/events";

    @Test
    @WithMockUser
    void getEvent_ReturnEvent_WhenIdIsExists() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.get(API_URL + "/" + events.get(0).getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(events.get(0).getId()))
                .andExpect(jsonPath("$.name").value(events.get(0).getName()))
                .andExpect(jsonPath("$.description").value(events.get(0).getDescription()));
    }

    @Test
    @WithMockUser
    void getEvent_ReturnNotFound_WhenIdIsNotExists() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.get(API_URL + "/123")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void getEvent_ReturnUnauthorized_WhenUserIsNotAuthenticated() throws Exception {

        mockMvc
                .perform(MockMvcRequestBuilders.get(API_URL + events.get(0).getId())
                        .header("Authorization", getToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void getEvents_ReturnsPageResponseDto_WhenRequestIsValid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(API_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("sort-by", "name")
                        .header("Authorization", getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Test Event4"))
                .andExpect(jsonPath("$.content[0].description").value("Test Description4"))
                .andExpect(jsonPath("$.content.size()").value(5));
    }

    @Test
    @WithMockUser
    void getEvents_ReturnsEmptyPageResponseDto_WhenNoEventsFound() throws Exception {
        eventRepository.deleteAll();

        mockMvc.perform(MockMvcRequestBuilders.get(API_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void getEvents_ReturnsBadRequest_WhenParamsAreInvalid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(API_URL)
                        .param("invalidParam", "invalidValue")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void getEvents_ReturnsFilteredEvents_WhenValidFilterParamsProvided() throws Exception {
        Event event = new Event();
        event.setName("Filtered Event123");
        event.setDescription("Filtered Description");
        event.setLocation("Filtered Location");
        event.setDate(LocalDate.now().plusDays(1));
        event.setStartTime(LocalTime.NOON);
        event.setEndTime(LocalTime.NOON.plusHours(2));
        event.setStatus(EventStatus.FINISHED);
        eventRepository.save(event);

        mockMvc.perform(MockMvcRequestBuilders.get(API_URL)
                        .param("status", "finished")
                        .param("sort-by", "name")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Filtered Event123"))
                .andExpect(jsonPath("$.content[0].description").value("Filtered Description"))
                .andExpect(jsonPath("$.content.size()").value(1));
    }

    @Test
    @WithMockUser
    void getEvents_ReturnsSortedEvents_WhenValidSortParamsProvided() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(API_URL)
                        .param("sort-by", "name")
                        .param("sort-order", "asc")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Test Event0"))
                .andExpect(jsonPath("$.content[4].name").value("Test Event4"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createEvent_ReturnsCreatedEvent_WhenParamsAreValidAndUserAdmin() throws Exception {
        CreateEventRequest request = new CreateEventRequest("Test Event", "Test Description", "Test Location",
                LocalDate.now(), LocalTime.NOON, LocalTime.NOON.plusHours(2), EventStatus.UPCOMING);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Event"))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    @WithMockUser
    void createEvent_ReturnUnauthorized_WhenUserIsNotAdmin() throws Exception {
        CreateEventRequest request = new CreateEventRequest("Test Event", "Test Description", "Test Location",
                LocalDate.now(), LocalTime.NOON, LocalTime.NOON.plusHours(2), EventStatus.UPCOMING);


        mockMvc.perform(MockMvcRequestBuilders.post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createEvent_ReturnBadRequest_WhenParamsAreNotValid() throws Exception {
        CreateEventRequest request = new CreateEventRequest("Test Event", "Test Description", "Test Location",
                LocalDate.now(), null, LocalTime.NOON.plusHours(2), EventStatus.UPCOMING);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateEvent_ReturnsUpdatedEvent_WhenParamsAreValidAndUserAdmin() throws Exception {
        CreateEventRequest request = new CreateEventRequest("Updated Event", "Updated Description", "Updated Location",
                LocalDate.now(), LocalTime.NOON, LocalTime.NOON.plusHours(2), EventStatus.UPCOMING);

        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/" + events.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Event"))
                .andExpect(jsonPath("$.description").value("Updated Description"));
    }

    @Test
    @WithMockUser
    void updateEvent_ReturnsUnauthorized_WhenUserIsNotAdmin() throws Exception {
        CreateEventRequest request = new CreateEventRequest("Updated Event", "Updated Description", "Updated Location",
                LocalDate.now(), LocalTime.NOON, LocalTime.NOON.plusHours(2), EventStatus.UPCOMING);

        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/" + events.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void updateEvent_ReturnsBadRequest_WhenParamsAreNotValid() throws Exception {
        CreateEventRequest request = new CreateEventRequest("Updated Event", "Updated Description", "Updated Location",
                LocalDate.now(), null, LocalTime.NOON.plusHours(2), EventStatus.UPCOMING);

        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/" + events.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void updateEvent_ReturnsNotFound_WhenEventIdDoesNotExist() throws Exception {
        CreateEventRequest request = new CreateEventRequest("Updated Event", "Updated Description", "Updated Location",
                LocalDate.now(), LocalTime.NOON, LocalTime.NOON.plusHours(2), EventStatus.UPCOMING);

        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateEvent_ReturnsBadRequest_WhenRequestBodyIsEmpty() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void patchEvent_ReturnsUpdatedEvent_WhenParamsAreValidAndUserAdmin() throws Exception {
        PatchEventRequest request = new PatchEventRequest("Patch Name", null, null, null, null, null, null);

        mockMvc.perform(MockMvcRequestBuilders.patch(API_URL + "/" + events.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Patch Name"))
                .andExpect(jsonPath("$.description").value(events.get(0).getDescription()))
                .andExpect(jsonPath("$.location").value(events.get(0).getLocation()))
                .andExpect(jsonPath("$.date").value(events.get(0).getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
    }

    @Test
    @WithMockUser
    void patchEvent_ReturnsUnauthorized_WhenUserIsNotAdmin() throws Exception {
        PatchEventRequest request = new PatchEventRequest("Patch Name", null, null, null, null, null, null);

        mockMvc.perform(MockMvcRequestBuilders.patch(API_URL + "/" + events.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void patchEvent_ReturnsEvent_WhenAllParamsAreNull() throws Exception {
        PatchEventRequest request = new PatchEventRequest(null, null, null, null, null, null, null);

        mockMvc.perform(MockMvcRequestBuilders.patch(API_URL + "/" + events.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(events.get(0).getName()))
                .andExpect(jsonPath("$.description").value(events.get(0).getDescription()))
                .andExpect(jsonPath("$.location").value(events.get(0).getLocation()))
                .andExpect(jsonPath("$.date").value(events.get(0).getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void patchEvent_ReturnsNotFound_WhenEventIdDoesNotExist() throws Exception {
        PatchEventRequest request = new PatchEventRequest("Patch Name", null, null, null, null, null, null);

        mockMvc.perform(MockMvcRequestBuilders.patch(API_URL + "/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void patchEvent_ReturnsBadRequest_WhenRequestBodyIsEmpty() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch(API_URL + "/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteEvent_ReturnsNoContent_WhenEventIdExists() throws Exception {
        String token = getToken();

        mockMvc.perform(MockMvcRequestBuilders.delete(API_URL + "/" + events.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token))
                .andExpect(status().isNoContent());

        mockMvc.perform(MockMvcRequestBuilders.get(API_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(4));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteEvent_ReturnsNotFound_WhenEventIdDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(API_URL + "/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void deleteEvent_ReturnsUnauthorized_WhenUserIsNotAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(API_URL + "/" + events.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken()))
                .andExpect(status().isUnauthorized());
    }
}
