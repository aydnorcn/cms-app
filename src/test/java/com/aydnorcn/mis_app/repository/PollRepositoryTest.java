package com.aydnorcn.mis_app.repository;

import com.aydnorcn.mis_app.entity.Poll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class PollRepositoryTest {

    @Autowired
    private PollRepository pollRepository;

    @Test
    void findById_ReturnsPoll_WhenIdIsExists() {
        Poll poll = new Poll();
        poll.setTitle("Test Poll");
        poll.setDescription("Test Description");

        pollRepository.save(poll);

        Optional<Poll> result = pollRepository.findById(poll.getId());

        assertTrue(result.isPresent());
        assertEquals(poll, result.get());
    }

    @Test
    void findById_ReturnsEmptyOptional_WhenIdDoesNotExist() {
        Optional<Poll> result = pollRepository.findById("nonexistent-id");

        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_ReturnsPolls_WhenSpecificationMatches() {
        Poll poll1 = new Poll();
        poll1.setTitle("Poll 1");
        poll1.setDescription("Description 1");
        poll1.setCreatedAt(LocalDateTime.now());
        poll1.setCreatedBy("User1");

        Poll poll2 = new Poll();
        poll2.setTitle("Poll 2");
        poll2.setDescription("Description 2");
        poll2.setCreatedAt(LocalDateTime.now());
        poll2.setCreatedBy("User2");

        pollRepository.save(poll1);
        pollRepository.save(poll2);

        assertEquals(2, pollRepository.findAll().size());
    }

    @Test
    void findAll_ReturnsEmptyPage_WhenNoPollsMatchSpecification() {
        Specification<Poll> specification = ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("title"), "Nonexistent Poll"));
        Pageable pageable = PageRequest.of(0, 10);

        Page<Poll> result = pollRepository.findAll(specification, pageable);

        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_ReturnsPagedPolls_WhenMultiplePageExists() {
        for (int i = 0; i < 15; i++) {
            Poll poll = new Poll();
            poll.setTitle("Poll" + i);
            poll.setDescription("Description" + i);
            poll.setCreatedBy("User" + i);
            poll.setCreatedAt(LocalDateTime.now());
            pollRepository.save(poll);
        }

        Specification<Poll> specification = (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("title"), "Poll%");
        Pageable pageable = PageRequest.of(0, 10);

        Page<Poll> result = pollRepository.findAll(specification, pageable);

        assertEquals(10, result.getNumberOfElements());
        assertEquals(15, result.getTotalElements());
    }


}
