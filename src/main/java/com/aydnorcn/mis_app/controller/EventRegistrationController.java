package com.aydnorcn.mis_app.controller;

import com.aydnorcn.mis_app.entity.EventRegistration;
import com.aydnorcn.mis_app.service.EventRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/event-registrations")
@RequiredArgsConstructor
public class EventRegistrationController {

    private final EventRegistrationService eventRegistrationService;

    @GetMapping("/{id}")
    public ResponseEntity<EventRegistration> getEventRegistrationById(@PathVariable String id) {
        EventRegistration eventRegistration = eventRegistrationService.getEventRegistrationById(id);
        return ResponseEntity.ok(eventRegistration);
    }

    @PostMapping
    public ResponseEntity<EventRegistration> saveEventRegistration(@RequestParam String eventId) {
        EventRegistration eventRegistration = eventRegistrationService.saveEventRegistration(eventId);
        return ResponseEntity.ok(eventRegistration);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEventRegistration(@PathVariable String id) {
        eventRegistrationService.deleteEventRegistration(id);
        return ResponseEntity.noContent().build();
    }
}
