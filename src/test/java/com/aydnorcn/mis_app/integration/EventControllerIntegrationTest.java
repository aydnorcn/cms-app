package com.aydnorcn.mis_app.integration;

import com.aydnorcn.mis_app.dto.event.CreateEventRequest;
import com.aydnorcn.mis_app.dto.event.PatchEventRequest;
import com.aydnorcn.mis_app.integration.support.EventControllerIntegrationTestSupport;
import com.aydnorcn.mis_app.utils.EventStatus;
import com.aydnorcn.mis_app.utils.MessageConstants;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@Transactional
class EventControllerIntegrationTest extends EventControllerIntegrationTestSupport {

    private final String API_URL = "/api/events";

    @Test
    void getEvent_ReturnEvent_WhenIdIsExists() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.get(API_URL + "/" + events.get(0).getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(events.get(0).getId()))
                .andExpect(jsonPath("$.name").value(events.get(0).getName()))
                .andExpect(jsonPath("$.description").value(events.get(0).getDescription()))
                .andDo(print());

    }

    @Test
    void getEvent_ReturnNotFound_WhenIdIsNotExists() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.get(API_URL + "/123")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(MessageConstants.EVENT_NOT_FOUND))
                .andDo(print());
    }

    @Test
    void getEvent_ReturnUnauthorized_WhenUserIsNotAuthenticated() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.get(API_URL + '/' + events.get(0).getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(MessageConstants.AUTHENTICATION_REQUIRED))
                .andDo(print());
    }

    @Test
    void getEvents_ReturnsPageResponseDto_WhenRequestIsValid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(API_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("sort-by", "id")
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("9"))
                .andExpect(jsonPath("$.content.size()").value(events.size()))
                .andDo(print());
    }

    @Test
    void getEvents_ReturnsEmptyPageResponseDto_WhenNoEventsFound() throws Exception {
//        eventRepository.deleteAll();

        mockMvc.perform(MockMvcRequestBuilders.get(API_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(events.size()))
                .andDo(print());
    }

    @Test
    void getEvents_ReturnsDefaultPageResponseDto_WhenParamsAreInvalid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(API_URL)
                        .param("invalidParam", "invalidValue")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(10))
                .andDo(print());
    }

    @Test
    void getEvents_ReturnsFilteredEvents_WhenValidFilterParamsProvided() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(API_URL)
                        .param("status", "finished")
                        .param("sort-by", "name")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(2))
                .andDo(print());
    }

    @Test
    void getEvents_ReturnsSortedEvents_WhenValidSortParamsProvided() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(API_URL)
                        .param("sort-by", "id")
                        .param("sort-order", "asc")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("1"))
                .andExpect(jsonPath("$.content[9].id").value("9"))
                .andDo(print());
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
                .andExpect(jsonPath("$.name").value("Test Event"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andDo(print());
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
                .andExpect(jsonPath("$.message").value(MessageConstants.UNAUTHORIZED_ACTION))
                .andDo(print());
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
                .andExpect(jsonPath("$.message").value(MessageConstants.UNAUTHORIZED_ACTION))
                .andDo(print());
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
                .andExpect(jsonPath("$.startTime").value(MessageConstants.START_TIME_NOT_NULL))
                .andDo(print());
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
                .andExpect(jsonPath("$.name").value("Updated Event"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andDo(print());
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
                .andExpect(jsonPath("$.message").value(MessageConstants.UNAUTHORIZED_ACTION))
                .andDo(print());
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
                .andExpect(jsonPath("$.startTime").value(MessageConstants.START_TIME_NOT_NULL))
                .andDo(print());
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
                .andExpect(jsonPath("$.message").value(MessageConstants.EVENT_NOT_FOUND))
                .andDo(print());
    }

    @Test
    void updateEvent_ReturnsBadRequest_WhenRequestBodyIsEmpty() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(MessageConstants.REQUEST_BODY_MISSING))
                .andDo(print());
    }

    @Test
    void patchEvent_ReturnsUpdatedEvent_WhenParamsAreValidAndUserAdmin() throws Exception {
        PatchEventRequest request = new PatchEventRequest("Patch Name", null, null, null, null, null, null);

        mockMvc.perform(MockMvcRequestBuilders.patch(API_URL + "/" + events.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Patch Name"))
                .andExpect(jsonPath("$.description").value(events.get(0).getDescription()))
                .andExpect(jsonPath("$.location").value(events.get(0).getLocation()))
                .andExpect(jsonPath("$.date").value(events.get(0).getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))))
                .andDo(print());
    }

    @Test
    void patchEvent_ReturnsForbidden_WhenUserIsNotAdmin() throws Exception {
        PatchEventRequest request = new PatchEventRequest("Patch Name", null, null, null, null, null, null);

        mockMvc.perform(MockMvcRequestBuilders.patch(API_URL + "/" + events.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(MessageConstants.UNAUTHORIZED_ACTION))
                .andDo(print());
    }

    @Test
    void patchEvent_ReturnsEvent_WhenAllParamsAreNull() throws Exception {
        PatchEventRequest request = new PatchEventRequest(null, null, null, null, null, null, null);

        mockMvc.perform(MockMvcRequestBuilders.patch(API_URL + "/" + events.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(events.get(0).getName()))
                .andExpect(jsonPath("$.description").value(events.get(0).getDescription()))
                .andExpect(jsonPath("$.location").value(events.get(0).getLocation()))
                .andExpect(jsonPath("$.date").value(events.get(0).getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))))
                .andDo(print());
    }

    @Test
    void patchEvent_ReturnsNotFound_WhenEventIdDoesNotExist() throws Exception {
        PatchEventRequest request = new PatchEventRequest("Patch Name", null, null, null, null, null, null);

        mockMvc.perform(MockMvcRequestBuilders.patch(API_URL + "/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(MessageConstants.EVENT_NOT_FOUND))
                .andDo(print());
    }

    @Test
    void patchEvent_ReturnsBadRequest_WhenRequestBodyIsEmpty() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch(API_URL + "/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(MessageConstants.REQUEST_BODY_MISSING))
                .andDo(print());
    }

    @Test
    void deleteEvent_ReturnsNoContent_WhenEventIdExists() throws Exception {
        String token = getToken(admin_email, password);

        mockMvc.perform(MockMvcRequestBuilders.delete(API_URL + "/" + events.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token))
                .andExpect(status().isNoContent());

        mockMvc.perform(MockMvcRequestBuilders.get(API_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(9))
                .andDo(print());
    }

    @Test
    void deleteEvent_ReturnsNotFound_WhenEventIdDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(API_URL + "/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(MessageConstants.EVENT_NOT_FOUND))
                .andDo(print());
    }

    @Test
    void deleteEvent_ReturnsForbidden_WhenUserIsNotAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(API_URL + "/" + events.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(MessageConstants.UNAUTHORIZED_ACTION))
                .andDo(print());
    }
}
