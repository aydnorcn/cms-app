package com.aydnorcn.mis_app.integration.support;

import com.aydnorcn.mis_app.TestUtils;
import com.aydnorcn.mis_app.entity.Poll;
import com.aydnorcn.mis_app.repository.PollRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.AuditorAware;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class PollControllerIntegrationTestSupport extends TestUtils {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected PollRepository pollRepository;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    private AuditorAware<String> auditorAware;

    protected static List<Poll> polls = new LinkedList<>();

    @BeforeEach
    void init() {
        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of("user"));

        polls = pollRepository.findAll();
    }
}
