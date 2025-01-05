package com.aydnorcn.mis_app.controller;

import com.aydnorcn.mis_app.dto.PageResponseDto;
import com.aydnorcn.mis_app.dto.event.CreateEventRequest;
import com.aydnorcn.mis_app.dto.event.EventResponse;
import com.aydnorcn.mis_app.dto.event.PatchEventRequest;
import com.aydnorcn.mis_app.entity.Event;
import com.aydnorcn.mis_app.service.EventService;
import com.aydnorcn.mis_app.utils.EventStatus;
import com.aydnorcn.mis_app.utils.params.EventParams;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class EventController {

    private final EventService eventService;

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponse> getEvent(@PathVariable String eventId) {
        Event event = eventService.getEventById(eventId);

        return ResponseEntity.ok(new EventResponse(event));
    }

    @GetMapping
    public ResponseEntity<PageResponseDto<EventResponse>> getEvents(@RequestParam Map<String, Object> searchParams) {
        EventParams params = new EventParams(searchParams);

        PageResponseDto<Event> events = eventService.getEvents(params);
        List<EventResponse> eventResponses = events.getContent().stream().map(EventResponse::new).toList();

        return ResponseEntity.ok(new PageResponseDto<>(eventResponses, events.getPageNo(), events.getPageSize(), events.getTotalElements(), events.getTotalPages()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventResponse> createEvent(@Validated @RequestBody CreateEventRequest request) {
        Event event = eventService.createEvent(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(new EventResponse(event));
    }

    @PutMapping("/{eventId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventResponse> updateEvent(@PathVariable String eventId, @Validated @RequestBody CreateEventRequest request) {
        Event event = eventService.updateEvent(eventId, request);

        return ResponseEntity.ok(new EventResponse(event));
    }

    @PatchMapping("/{eventId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventResponse> patchEvent(@PathVariable String eventId, @Validated @RequestBody PatchEventRequest request) {
        Event event = eventService.patchEvent(eventId, request);

        return ResponseEntity.ok(new EventResponse(event));
    }

    @DeleteMapping("/{eventId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEvent(@PathVariable String eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }
}