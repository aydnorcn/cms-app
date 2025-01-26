package com.aydnorcn.mis_app.integration.support;

import com.aydnorcn.mis_app.TestUtils;
import com.aydnorcn.mis_app.entity.Event;
import com.aydnorcn.mis_app.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.AuditorAware;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class EventControllerIntegrationTestSupport extends TestUtils {

    @Autowired
    protected EventRepository eventRepository;

    @MockitoBean
    private AuditorAware<String> auditorAware;

    protected static List<Event> events = new LinkedList<>();


    @BeforeEach
    void init() {
        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of("user"));

        events = eventRepository.findAll();
    }
}
