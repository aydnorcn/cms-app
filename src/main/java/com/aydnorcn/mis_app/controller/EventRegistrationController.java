package com.aydnorcn.mis_app.controller;

import com.aydnorcn.mis_app.dto.PageResponseDto;
import com.aydnorcn.mis_app.dto.event_registration.EventRegistrationResponse;
import com.aydnorcn.mis_app.entity.EventRegistration;
import com.aydnorcn.mis_app.service.EventRegistrationService;
import com.aydnorcn.mis_app.utils.params.EventRegistrationParams;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/event-registrations")
@RequiredArgsConstructor
public class EventRegistrationController {

    private final EventRegistrationService eventRegistrationService;

    @GetMapping("/{id}")
    public ResponseEntity<EventRegistrationResponse> getEventRegistrationById(@PathVariable String id) {
        EventRegistration eventRegistration = eventRegistrationService.getEventRegistrationById(id);
        return ResponseEntity.ok(new EventRegistrationResponse(eventRegistration));
    }

    @GetMapping
    public ResponseEntity<PageResponseDto<EventRegistrationResponse>> getEventRegistrations(@RequestParam Map<String, Object> searchParams) {
        EventRegistrationParams params = new EventRegistrationParams(searchParams);

        PageResponseDto<EventRegistration> eventRegistrations = eventRegistrationService.getEventRegistrations(params);
        List<EventRegistrationResponse> eventRegistrationResponses = eventRegistrations.getContent()
                .stream()
                .map(EventRegistrationResponse::new)
                .toList();

        return ResponseEntity.ok(new PageResponseDto<>(eventRegistrationResponses, eventRegistrations.getPageNo(), eventRegistrations.getPageSize(), eventRegistrations.getTotalElements(), eventRegistrations.getTotalPages()));
    }

    @PostMapping
    public ResponseEntity<EventRegistrationResponse> saveEventRegistration(@RequestParam String eventId) {
        EventRegistration eventRegistration = eventRegistrationService.saveEventRegistration(eventId);
        return ResponseEntity.ok(new EventRegistrationResponse(eventRegistration));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEventRegistration(@PathVariable String id) {
        eventRegistrationService.deleteEventRegistration(id);
        return ResponseEntity.noContent().build();
    }
}
