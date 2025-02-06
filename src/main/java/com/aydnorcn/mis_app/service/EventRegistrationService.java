package com.aydnorcn.mis_app.service;

import com.aydnorcn.mis_app.dto.PageResponseDto;
import com.aydnorcn.mis_app.entity.Event;
import com.aydnorcn.mis_app.entity.EventRegistration;
import com.aydnorcn.mis_app.exception.ResourceNotFoundException;
import com.aydnorcn.mis_app.filter.EventRegistrationFilter;
import com.aydnorcn.mis_app.repository.EventRegistrationRepository;
import com.aydnorcn.mis_app.utils.params.EventRegistrationParams;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventRegistrationService {

    private final EventRegistrationRepository eventRegistrationRepository;
    private final EventService eventService;
    private final UserService userService;
    private final UserContextService userContextService;

    public EventRegistration getEventRegistrationById(String registrationId){
        return eventRegistrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Event registration not found!"));
    }

    public PageResponseDto<EventRegistration> getEventRegistrations(EventRegistrationParams params){
        var event = (params.getEventId() == null) ? null : eventService.getEventById(params.getEventId());
        var user = (params.getUserId() == null) ? null : userService.getUserById(params.getUserId());

        Specification<EventRegistration> specification = EventRegistrationFilter.filter(user, event);

        Page<EventRegistration> eventRegistrations = eventRegistrationRepository.findAll(specification, PageRequest.of(params.getPageNo(), params.getPageSize(), params.getSort()));

        return new PageResponseDto<>(eventRegistrations);
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