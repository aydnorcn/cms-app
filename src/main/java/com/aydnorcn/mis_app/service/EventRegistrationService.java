package com.aydnorcn.mis_app.service;

import com.aydnorcn.mis_app.entity.Event;
import com.aydnorcn.mis_app.entity.EventRegistration;
import com.aydnorcn.mis_app.exception.ResourceNotFoundException;
import com.aydnorcn.mis_app.repository.EventRegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventRegistrationService {

    private final EventRegistrationRepository eventRegistrationRepository;
    private final EventService eventService;
    private final UserContextService userContextService;

    public EventRegistration getEventRegistrationById(String registrationId){
        return eventRegistrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Event registration not found!"));
    }

    public EventRegistration saveEventRegistration(String eventId){
        Event event = eventService.getEventById(eventId);

        EventRegistration eventRegistration = new EventRegistration(userContextService.getCurrentAuthenticatedUser(), event);

        return eventRegistrationRepository.save(eventRegistration);
    }

    public void deleteEventRegistration(String registrationId){
        EventRegistration eventRegistration = getEventRegistrationById(registrationId);

        eventRegistrationRepository.delete(eventRegistration);
    }
}