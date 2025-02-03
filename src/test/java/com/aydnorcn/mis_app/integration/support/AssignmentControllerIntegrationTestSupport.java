package com.aydnorcn.mis_app.integration.support;

import com.aydnorcn.mis_app.TestUtils;
import com.aydnorcn.mis_app.entity.Assignment;
import com.aydnorcn.mis_app.entity.Event;
import com.aydnorcn.mis_app.entity.User;
import com.aydnorcn.mis_app.repository.AssignmentRepository;
import com.aydnorcn.mis_app.repository.EventRepository;
import com.aydnorcn.mis_app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest
@AutoConfigureMockMvc
public abstract class AssignmentControllerIntegrationTestSupport extends TestUtils {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    protected static List<Assignment> assignments;
    protected static List<Event> events;
    protected User user;

    @BeforeEach
    void init() {
        assignments = assignmentRepository.findAll();
        events = eventRepository.findAll();
        user = userRepository.findById("user-3").get();
    }
}