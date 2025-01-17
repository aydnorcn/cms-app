package com.aydnorcn.mis_app.service;

import com.aydnorcn.mis_app.dto.PageResponseDto;
import com.aydnorcn.mis_app.dto.event.CreateEventRequest;
import com.aydnorcn.mis_app.dto.event.PatchEventRequest;
import com.aydnorcn.mis_app.entity.Event;
import com.aydnorcn.mis_app.exception.ResourceNotFoundException;
import com.aydnorcn.mis_app.repository.EventRepository;
import com.aydnorcn.mis_app.utils.EventStatus;
import com.aydnorcn.mis_app.utils.params.EventParams;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;


    @Test
    void getEventById_ReturnsEvent_WhenEventIdExists() {
        // given
        String eventId = "1";
        Event event = new Event();
        event.setId(eventId);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        // when
        Event result = eventService.getEventById(eventId);

        // then
        assertEquals(event, result);
    }

    @Test
    void getEventById_ThrowsResourceNotFoundException_WhenEventIdDoesNotExists() {
        String eventId = "1";

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> eventService.getEventById(eventId));
    }

    @Test
    void getEvents_ReturnsPageResponseDto_WhenRequestIsValid() {
        EventParams params = new EventParams();
        params.setPageNo(0);
        params.setPageSize(10);
        params.setSortBy("name");
        params.setSortOrder("asc");

        List<Event> events = List.of(new Event(), new Event());
        Page<Event> page = new PageImpl<>(events);
        when(eventRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);

        PageResponseDto<Event> result = eventService.getEvents(params);

        assertEquals(page.getContent(), result.getContent());
    }

    @Test
    void getEvents_ReturnsEmptyPageResponseDto_WhenNoEventsMatch() {
        EventParams params = new EventParams();
        params.setPageNo(0);
        params.setPageSize(10);
        params.setSortBy("name");
        params.setSortOrder("asc");

        Page<Event> emptyPage = Page.empty();
        when(eventRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(emptyPage);

        PageResponseDto<Event> result = eventService.getEvents(params);

        assertTrue(result.getContent().isEmpty());
    }

    @Test
    void createEvent_SavesAndReturnsEvent_WhenRequestIsValid() {
        CreateEventRequest request = new CreateEventRequest();
        request.setName("Event Name");
        request.setDescription("Event Description");
        request.setLocation("Event Location");
        request.setDate(LocalDate.now());
        request.setStartTime(LocalTime.now());
        request.setEndTime(LocalTime.now().plusHours(2));
        request.setStatus(EventStatus.UPCOMING);

        Event event = new Event();
        event.setId("1");
        event.setName(request.getName());
        event.setDescription(request.getDescription());
        event.setLocation(request.getLocation());
        event.setDate(request.getDate());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
        event.setStatus(request.getStatus());

        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event result = eventService.createEvent(request);

        assertEquals(event, result);
    }

    @Test
    void updateEvent_SavesAndReturnEvent_WhenRequestIsValid() {
        String eventId = "1";

        String eventName = "Event Name";
        String eventDescription = "Event Description";

        CreateEventRequest request = new CreateEventRequest();
        request.setName(eventName);
        request.setDescription(eventDescription);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(new Event()));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Event result = eventService.updateEvent(eventId, request);


        assertEquals(eventName, result.getName());
        assertEquals(eventDescription, result.getDescription());
    }

    @Test
    void updateEvent_ThrowsResourceNotFoundException_WhenEventIdDoesNotExists() {
        String eventId = "1";

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> eventService.updateEvent(eventId, new CreateEventRequest()));
    }

    @Test
    void patchEvent_ThrowsResourceNotFoundException_WhenEventIdDoesNotExists() {
        String eventId = "1";

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> eventService.patchEvent(eventId, new PatchEventRequest()));
    }

    @Test
    void patchEvent_SavesAndReturnEvent_WhenRequestIsValid() {
        String eventId = "1";

        String eventName = "Event Name";
        String eventDescription = "Event Description";
        String eventLocation = "Event Location";

        PatchEventRequest request = new PatchEventRequest();
        request.setName(eventName);
        request.setDescription(eventDescription);

        Event event = new Event();
        event.setId(eventId);
        event.setName("Old name");
        event.setDescription("Old description");
        event.setLocation(eventLocation);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Event result = eventService.patchEvent(eventId, request);

        assertEquals(eventName, result.getName());
        assertEquals(eventDescription, result.getDescription());
        assertEquals(eventLocation, result.getLocation());
    }

    @Test
    void deleteEvent_DeletesEvent_WhenEventIdExists() {
        String eventId = "1";

        Event event = new Event();
        event.setId(eventId);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        eventService.deleteEvent(eventId);

        verify(eventRepository).delete(event);
    }

    @Test
    void deleteEvent_ThrowsResourceNotFoundException_WhenEventIdDoesNotExists() {
        String eventId = "1";

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> eventService.deleteEvent(eventId));
    }

}