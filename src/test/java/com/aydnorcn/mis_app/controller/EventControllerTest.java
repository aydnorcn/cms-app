package com.aydnorcn.mis_app.controller;

import com.aydnorcn.mis_app.dto.PageResponseDto;
import com.aydnorcn.mis_app.dto.event.CreateEventRequest;
import com.aydnorcn.mis_app.dto.event.PatchEventRequest;
import com.aydnorcn.mis_app.entity.Event;
import com.aydnorcn.mis_app.exception.ResourceNotFoundException;
import com.aydnorcn.mis_app.service.EventService;
import com.aydnorcn.mis_app.utils.EventStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class EventControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private EventService eventService;

    @Autowired
    private ObjectMapper objectMapper;

    private Event event;
    private CreateEventRequest request;

    @BeforeEach
    void init() {
        event = new Event();
        event.setId("1");
        event.setName("Test Event");
        event.setDescription("Test Description");

        request = new CreateEventRequest();
        request.setName("Test Event Request");
        request.setDescription("Test Description Request");
        request.setLocation("Test Location Request");
        request.setDate(LocalDate.now().plusDays(3));
        request.setStartTime(LocalTime.NOON);
        request.setEndTime(LocalTime.NOON.plusHours(2));
        request.setStatus(EventStatus.UPCOMING);
    }

    @Test
    void getEvent_ReturnEvent_WhenIdIsExists() throws Exception {
        when(eventService.getEventById("1")).thenReturn(event);

        mvc
                .perform(MockMvcRequestBuilders.get("/api/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Event"))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    void getEvent_ReturnIsNotFound_WhenIdDoesNotExists() throws Exception {
        when(eventService.getEventById("1")).thenThrow(new ResourceNotFoundException("Event not found"));

        mvc
                .perform(MockMvcRequestBuilders.get("/api/events/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getEvents_ReturnsPageResponseDto_WhenRequestIsValid() throws Exception {
        when(eventService.getEvents(any())).thenReturn(new PageResponseDto<>(List.of(event), 0, 10, 1, 1));

        mvc
                .perform(MockMvcRequestBuilders.get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Test Event"))
                .andExpect(jsonPath("$.content[0].description").value("Test Description"));
    }


    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createEvent_ReturnCreatedEvent_WhenParamsAreValidAndUserAdmin() throws Exception {
        when(eventService.createEvent(any(CreateEventRequest.class))).thenReturn(event);

        mvc
                .perform(MockMvcRequestBuilders.post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Event"))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    @WithMockUser
    void createEvent_ReturnUnauthorized_WhenUserIsNotAdmin() throws Exception {
        mvc
                .perform(MockMvcRequestBuilders.post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createEvent_ReturnBadRequest_WhenParamsAreNotValid() throws Exception {
        request.setStartTime(null);

        when(eventService.createEvent(any(CreateEventRequest.class))).thenReturn(event);

        mvc
                .perform(MockMvcRequestBuilders.post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void updateEvent_ReturnUpdatedEvent_WhenParamsAreValidAndUserAdmin() throws Exception {
        Event updatedEvent = new Event();
        updatedEvent.setId("1");
        updatedEvent.setName("Updated Event");
        updatedEvent.setDescription("Updated Description");

        when(eventService.updateEvent(anyString(), any(CreateEventRequest.class))).thenReturn(updatedEvent);

        mvc
                .perform(MockMvcRequestBuilders.put("/api/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Event"))
                .andExpect(jsonPath("$.description").value("Updated Description"));
    }

    @Test
    @WithMockUser
    void updateEvent_ReturnUnauthorized_WhenUserIsNotAdmin() throws Exception {
        mvc
                .perform(MockMvcRequestBuilders.put("/api/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateEvent_ReturnBadRequest_WhenParamsAreNotValid() throws Exception {
        request.setStartTime(null);

        when(eventService.updateEvent(anyString(), any(CreateEventRequest.class))).thenReturn(event);

        mvc
                .perform(MockMvcRequestBuilders.put("/api/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void updateEvent_ReturnNotFound_WhenIdDoesNotExist() throws Exception {
        when(eventService.updateEvent(anyString(), any(CreateEventRequest.class))).thenThrow(new ResourceNotFoundException("Event not found"));

        mvc
                .perform(MockMvcRequestBuilders.put("/api/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void patchEvent_ReturnsUpdatedEvent_WhenParamsAreValidAndUserAdmin() throws Exception {
        PatchEventRequest patchEventRequest = new PatchEventRequest();
        patchEventRequest.setName("Updated Event");
        patchEventRequest.setDescription("Updated Description");

        Event updatedEvent = new Event();
        updatedEvent.setId("1");
        updatedEvent.setName("Updated Event");
        updatedEvent.setDescription("Updated Description");

        when(eventService.patchEvent(anyString(), any(PatchEventRequest.class))).thenReturn(updatedEvent);

        mvc.perform(MockMvcRequestBuilders.patch("/api/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchEventRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Event"))
                .andExpect(jsonPath("$.description").value("Updated Description"));
    }

    @Test
    @WithMockUser
    void patchEvent_ReturnsUnauthorized_WhenUserIsNotAdmin() throws Exception {
        PatchEventRequest patchEventRequest = new PatchEventRequest();
        patchEventRequest.setName("Updated Event");
        patchEventRequest.setDescription("Updated Description");

        mvc.perform(MockMvcRequestBuilders.patch("/api/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchEventRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void patchEvent_ReturnsEvent_WhenSomeParamsAreNull() throws Exception {
        PatchEventRequest patchEventRequest = new PatchEventRequest();
        patchEventRequest.setName(null); // Invalid parameter

        when(eventService.patchEvent(anyString(), any(PatchEventRequest.class))).thenReturn(event);

        mvc.perform(MockMvcRequestBuilders.patch("/api/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchEventRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Event"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void patchEvent_ReturnsNotFound_WhenEventIdDoesNotExist() throws Exception {
        PatchEventRequest patchEventRequest = new PatchEventRequest();
        patchEventRequest.setName("Updated Event");
        patchEventRequest.setDescription("Updated Description");

        when(eventService.patchEvent(anyString(), any(PatchEventRequest.class))).thenThrow(new ResourceNotFoundException("Event not found"));

        mvc.perform(MockMvcRequestBuilders.patch("/api/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchEventRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void deleteEvent_ReturnsNoContent_WhenEventIdExists() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/api/events/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void deleteEvent_ReturnsNotFound_WhenEventIdDoesNotExist() throws Exception {
       doThrow(new ResourceNotFoundException("Event not found")).when(eventService).deleteEvent("1");

        mvc.perform(MockMvcRequestBuilders.delete("/api/events/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void deleteEvent_ReturnsUnauthorized_WhenUserIsNotAdmin() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/api/events/1"))
                .andExpect(status().isUnauthorized());
    }

}
