package com.aydnorcn.mis_app.service;

import com.aydnorcn.mis_app.dto.PageResponseDto;
import com.aydnorcn.mis_app.dto.event.CreateEventRequest;
import com.aydnorcn.mis_app.dto.event.PatchEventRequest;
import com.aydnorcn.mis_app.entity.Event;
import com.aydnorcn.mis_app.exception.ResourceNotFoundException;
import com.aydnorcn.mis_app.filter.EventFilter;
import com.aydnorcn.mis_app.repository.EventRepository;
import com.aydnorcn.mis_app.utils.MessageConstants;
import com.aydnorcn.mis_app.utils.params.EventParams;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserService userService;

    @Cacheable(value = "event", key = "#eventId")
    public Event getEventById(String eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.EVENT_NOT_FOUND));
    }

    public PageResponseDto<Event> getEvents(EventParams params) {
        var user = (params.getCreatedBy() == null) ? null : userService.getUserById(params.getCreatedBy());

        Specification<Event> specification = EventFilter.filter(params.getLocation(), params.getDate(), params.getStartAfter(), params.getEndBefore(), params.getStatus(), user);

        Page<Event> page = eventRepository.findAll(specification, PageRequest.of(params.getPageNo(), params.getPageSize(), params.getSort()));

        return new PageResponseDto<>(page);
    }

    public Event createEvent(CreateEventRequest request) {
        Event event = new Event();

        updateEventFields(event, request);

        return eventRepository.save(event);
    }


    @CachePut(value = "event", key = "#eventId")
    public Event updateEvent(String eventId, CreateEventRequest request) {
        Event updateEvent = getEventById(eventId);

        updateEventFields(updateEvent, request);

        return eventRepository.save(updateEvent);
    }

    @CachePut(value = "event", key = "#eventId")
    public Event patchEvent(String eventId, PatchEventRequest request) {
        Event updateEvent = getEventById(eventId);

        patchEventFields(request, updateEvent);

        return eventRepository.save(updateEvent);
    }

    @CacheEvict(value = "event", key = "#eventId")
    public void deleteEvent(String eventId) {
        Event event = getEventById(eventId);
        eventRepository.delete(event);
    }

    private void updateEventFields(Event updateEvent, CreateEventRequest request) {
        updateEvent.setName(request.getName());
        updateEvent.setDescription(request.getDescription());
        updateEvent.setLocation(request.getLocation());
        updateEvent.setDate(request.getDate());
        updateEvent.setStartTime(request.getStartTime());
        updateEvent.setEndTime(request.getEndTime());
        updateEvent.setStatus(request.getStatus());
    }

    private void patchEventFields(PatchEventRequest request, Event updateEvent) {
        if (request.getName() != null) updateEvent.setName(request.getName());
        if (request.getDescription() != null) updateEvent.setDescription(request.getDescription());
        if (request.getLocation() != null) updateEvent.setLocation(request.getLocation());
        if (request.getDate() != null) updateEvent.setDate(request.getDate());
        if (request.getStartTime() != null) updateEvent.setStartTime(request.getStartTime());
        if (request.getEndTime() != null) updateEvent.setEndTime(request.getEndTime());
        if (request.getStatus() != null) updateEvent.setStatus(request.getStatus());
    }
}