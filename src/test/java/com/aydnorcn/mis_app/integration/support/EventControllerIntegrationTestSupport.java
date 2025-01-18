package com.aydnorcn.mis_app.integration.support;

import com.aydnorcn.mis_app.entity.Event;
import com.aydnorcn.mis_app.jwt.JwtTokenProvider;
import com.aydnorcn.mis_app.repository.EventRepository;
import com.aydnorcn.mis_app.utils.EventStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc()
public abstract class EventControllerIntegrationTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected EventRepository eventRepository;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    private AuditorAware<String> auditorAware;

    @MockitoBean
    protected JwtTokenProvider provider;

    protected List<Event> events = new LinkedList<>();

    @BeforeEach
    void init() {
        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of("user"));
        events.clear();
        eventRepository.deleteAll();

        for (int i = 0; i < 5; i++) {
            Event event = new Event();
            event.setName("Test Event" + i);
            event.setDescription("Test Description" + i);
            event.setLocation("Test Location" + i);
            event.setDate(LocalDate.now());
            event.setStartTime(LocalTime.now().plusHours(3 + i));
            event.setEndTime(LocalTime.now().plusHours(5 + i));
            event.setStatus(EventStatus.UPCOMING);

            events.add(event);
        }
        eventRepository.saveAll(events);
    }

    protected String getToken() {
        return "Bearer " + provider.generateToken(new UsernamePasswordAuthenticationToken(
                SecurityContextHolder.getContext().getAuthentication().getName(),
                null,
                SecurityContextHolder.getContext().getAuthentication().getAuthorities()));
    }
}
