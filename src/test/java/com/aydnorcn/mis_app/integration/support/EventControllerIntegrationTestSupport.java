package com.aydnorcn.mis_app.integration.support;

import com.aydnorcn.mis_app.TestUtils;
import com.aydnorcn.mis_app.entity.Event;
import com.aydnorcn.mis_app.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.LinkedList;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class EventControllerIntegrationTestSupport extends TestUtils {

    @Autowired
    protected EventRepository eventRepository;

    protected static List<Event> events = new LinkedList<>();


    @BeforeEach
    void init() {
        events = eventRepository.findAll();
    }
}
