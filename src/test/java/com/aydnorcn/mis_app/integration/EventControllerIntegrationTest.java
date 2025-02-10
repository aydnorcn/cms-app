package com.aydnorcn.mis_app.integration;

import com.aydnorcn.mis_app.dto.event.CreateEventRequest;
import com.aydnorcn.mis_app.dto.event.PatchEventRequest;
import com.aydnorcn.mis_app.entity.Event;
import com.aydnorcn.mis_app.integration.support.EventControllerIntegrationTestSupport;
import com.aydnorcn.mis_app.utils.EventStatus;
import com.aydnorcn.mis_app.utils.MessageConstants;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
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
    void getEvent_ReturnEvent_WhenIdIsExists() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.get(API_URL + "/" + events.get(0).getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(events.get(0).getId()))
                .andExpect(jsonPath("$.data.name").value(events.get(0).getName()))
                .andExpect(jsonPath("$.data.description").value(events.get(0).getDescription()));
    }

    @Test
    void getEvent_ReturnNotFound_WhenIdIsNotExists() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.get(API_URL + "/123")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data.message").value(MessageConstants.EVENT_NOT_FOUND));
    }

    @Test
    void getEvent_ReturnUnauthorized_WhenUserIsNotAuthenticated() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.get(API_URL + '/' + events.get(0).getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.data.message").value(MessageConstants.AUTHENTICATION_REQUIRED));
    }

    @Test
    void getEvents_ReturnsPageResponseDto_WhenRequestIsValid() throws Exception {
        String firstEvent = events.stream().map(Event::getId).sorted().findFirst().get();

        mockMvc.perform(MockMvcRequestBuilders.get(API_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("sort-by", "id")
                        .param("sort-order", "asc")
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(firstEvent))
                .andExpect(jsonPath("$.data.content.size()").value(events.size()));
    }

    @Test
    void getEvents_ReturnsEmptyPageResponseDto_WhenNoEventsFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(API_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(events.size()));
    }

    @Test
    void getEvents_ReturnsDefaultPageResponseDto_WhenParamsAreInvalid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(API_URL)
                        .param("invalidParam", "invalidValue")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(10));
    }

    @Test
    void getEvents_ReturnsFilteredEvents_WhenValidFilterParamsProvided() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(API_URL)
                        .param("status", "finished")
                        .param("sort-by", "name")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.size()").value(2));
    }

    @Test
    void getEvents_ReturnsSortedEvents_WhenValidSortParamsProvided() throws Exception {
        String firstEventId = events.stream().map(Event::getId).min(String::compareTo).get();

        mockMvc.perform(MockMvcRequestBuilders.get(API_URL)
                        .param("sort-by", "id")
                        .param("sort-order", "asc")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(firstEventId));
    }

    @Test
    void createEvent_ReturnsCreatedEvent_WhenParamsAreValidAndUserAdmin() throws Exception {
        CreateEventRequest request = new CreateEventRequest("Test Event", "Test Description", "Test Location",
                LocalDate.now(), LocalTime.NOON, LocalTime.NOON.plusHours(2), EventStatus.UPCOMING);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(admin_email, password))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Test Event"))
                .andExpect(jsonPath("$.data.description").value("Test Description"));
    }

    @Test
    void createEvent_ReturnsUnauthorized_WhenUserIsNotAuthenticated() throws Exception {
        CreateEventRequest request = new CreateEventRequest("Test Event", "Test Description", "Test Location",
                LocalDate.now(), LocalTime.NOON, LocalTime.NOON.plusHours(2), EventStatus.UPCOMING);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(MessageConstants.UNAUTHORIZED_ACTION));
    }

    @Test
    void createEvent_ReturnsForbidden_WhenUserIsNotAdmin() throws Exception {
        CreateEventRequest request = new CreateEventRequest("Test Event", "Test Description", "Test Location",
                LocalDate.now(), LocalTime.NOON, LocalTime.NOON.plusHours(2), EventStatus.UPCOMING);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(MessageConstants.UNAUTHORIZED_ACTION));
    }

    @Test
    void createEvent_ReturnBadRequest_WhenParamsAreNotValid() throws Exception {
        CreateEventRequest request = new CreateEventRequest("Test Event", "Test Description", "Test Location",
                LocalDate.now(), null, LocalTime.NOON.plusHours(2), EventStatus.UPCOMING);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.message.startTime").value(MessageConstants.START_TIME_NOT_NULL));
    }

    @Test
    void updateEvent_ReturnsUpdatedEvent_WhenParamsAreValidAndUserAdmin() throws Exception {
        CreateEventRequest request = new CreateEventRequest("Updated Event", "Updated Description", "Updated Location",
                LocalDate.now(), LocalTime.NOON, LocalTime.NOON.plusHours(2), EventStatus.UPCOMING);

        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/" + events.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Updated Event"))
                .andExpect(jsonPath("$.data.description").value("Updated Description"));
    }

    @Test
    void updateEvent_ReturnsForbidden_WhenUserIsNotAdmin() throws Exception {
        CreateEventRequest request = new CreateEventRequest("Updated Event", "Updated Description", "Updated Location",
                LocalDate.now(), LocalTime.NOON, LocalTime.NOON.plusHours(2), EventStatus.UPCOMING);

        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/" + events.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(MessageConstants.UNAUTHORIZED_ACTION));
    }

    @Test
    void updateEvent_ReturnsBadRequest_WhenParamsAreNotValid() throws Exception {
        CreateEventRequest request = new CreateEventRequest("Updated Event", "Updated Description", "Updated Location",
                LocalDate.now(), null, LocalTime.NOON.plusHours(2), EventStatus.UPCOMING);

        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/" + events.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.message.startTime").value(MessageConstants.START_TIME_NOT_NULL));
    }

    @Test
    void updateEvent_ReturnsNotFound_WhenEventIdDoesNotExist() throws Exception {
        CreateEventRequest request = new CreateEventRequest("Updated Event", "Updated Description", "Updated Location",
                LocalDate.now(), LocalTime.NOON, LocalTime.NOON.plusHours(2), EventStatus.UPCOMING);

        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(MessageConstants.EVENT_NOT_FOUND));
    }

    @Test
    void updateEvent_ReturnsBadRequest_WhenRequestBodyIsEmpty() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(MessageConstants.REQUEST_BODY_MISSING));
    }

    @Test
    void patchEvent_ReturnsUpdatedEvent_WhenParamsAreValidAndUserAdmin() throws Exception {
        PatchEventRequest request = new PatchEventRequest("Patch Name", null, null, null, null, null, null);

        mockMvc.perform(MockMvcRequestBuilders.patch(API_URL + "/" + events.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Patch Name"))
                .andExpect(jsonPath("$.data.description").value(events.get(0).getDescription()))
                .andExpect(jsonPath("$.data.location").value(events.get(0).getLocation()))
                .andExpect(jsonPath("$.data.date").value(events.get(0).getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
    }

    @Test
    void patchEvent_ReturnsForbidden_WhenUserIsNotAdmin() throws Exception {
        PatchEventRequest request = new PatchEventRequest("Patch Name", null, null, null, null, null, null);

        mockMvc.perform(MockMvcRequestBuilders.patch(API_URL + "/" + events.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(MessageConstants.UNAUTHORIZED_ACTION));
    }

    @Test
    void patchEvent_ReturnsEvent_WhenAllParamsAreNull() throws Exception {
        PatchEventRequest request = new PatchEventRequest(null, null, null, null, null, null, null);

        mockMvc.perform(MockMvcRequestBuilders.patch(API_URL + "/" + events.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(events.get(0).getName()))
                .andExpect(jsonPath("$.data.description").value(events.get(0).getDescription()))
                .andExpect(jsonPath("$.data.location").value(events.get(0).getLocation()))
                .andExpect(jsonPath("$.data.date").value(events.get(0).getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
    }

    @Test
    void patchEvent_ReturnsNotFound_WhenEventIdDoesNotExist() throws Exception {
        PatchEventRequest request = new PatchEventRequest("Patch Name", null, null, null, null, null, null);

        mockMvc.perform(MockMvcRequestBuilders.patch(API_URL + "/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(MessageConstants.EVENT_NOT_FOUND));
    }

    @Test
    void patchEvent_ReturnsBadRequest_WhenRequestBodyIsEmpty() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch(API_URL + "/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(MessageConstants.REQUEST_BODY_MISSING));
    }

    @Test
    void deleteEvent_ReturnsNoContent_WhenEventIdExists() throws Exception {
        String token = getToken(admin_email, password);

        mockMvc.perform(MockMvcRequestBuilders.delete(API_URL + "/" + events.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteEvent_ReturnsNotFound_WhenEventIdDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(API_URL + "/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(MessageConstants.EVENT_NOT_FOUND));
    }

    @Test
    void deleteEvent_ReturnsForbidden_WhenUserIsNotAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(API_URL + "/" + events.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(MessageConstants.UNAUTHORIZED_ACTION));
    }
}
