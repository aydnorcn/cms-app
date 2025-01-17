package com.aydnorcn.mis_app.repository;

import com.aydnorcn.mis_app.entity.Event;
import com.aydnorcn.mis_app.filter.EventFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class EventRepositoryTest {


    @Autowired
    private EventRepository eventRepository;

    @Test
    void findById_ReturnsEvent_WhenIdIsExists(){
        Event event = new Event();
        event.setName("Test Event");
        event.setLocation("Test Location");

        eventRepository.save(event);

        Optional<Event> result = eventRepository.findById(event.getId());

        assertTrue(result.isPresent());
        assertEquals(event, result.get());
    }

    @Test
    void findById_ReturnsEmptyOptional_WhenIdDoesNotExist() {
        Optional<Event> result = eventRepository.findById("nonexistent-id");

        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_ReturnsEvents_WhenSpecificationMatches() {
        Event event1 = new Event();
        event1.setName("Event 1");
        event1.setLocation("Location 1");
        event1.setCreatedBy("User1");

        Event event2 = new Event();
        event2.setName("Event 2");
        event2.setLocation("Location 2");
        event2.setCreatedBy("User2");

        eventRepository.save(event1);
        eventRepository.save(event2);

        Specification<Event> a = EventFilter.filter("Location 1", null, null, null, null, null);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Event> result = eventRepository.findAll(a, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(event1, result.getContent().get(0));
    }

    @Test
    void findAll_ReturnsEmptyPage_WhenNoEventsMatchSpecification() {
        Specification<Event> specification = (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("location"), "Nonexistent Location");
        Pageable pageable = PageRequest.of(0, 10);

        Page<Event> result = eventRepository.findAll(specification, pageable);

        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_ReturnsPagedEvents_WhenMultiplePagesExist() {
        for (int i = 1; i <= 15; i++) {
            Event event = new Event();
            event.setName("Event " + i);
            event.setLocation("Location " + i);
            event.setCreatedBy("User" + i);
            eventRepository.save(event);
        }

        Specification<Event> specification = (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("name"), "Event%");
        Pageable pageable = PageRequest.of(0, 10);

        Page<Event> result = eventRepository.findAll(specification, pageable);

        assertEquals(10, result.getNumberOfElements());
        assertEquals(15, result.getTotalElements());
    }
}