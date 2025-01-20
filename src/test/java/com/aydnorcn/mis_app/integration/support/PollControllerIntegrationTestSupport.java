package com.aydnorcn.mis_app.integration.support;

import com.aydnorcn.mis_app.entity.Option;
import com.aydnorcn.mis_app.entity.Poll;
import com.aydnorcn.mis_app.jwt.JwtTokenProvider;
import com.aydnorcn.mis_app.repository.PollRepository;
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

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class PollControllerIntegrationTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected PollRepository pollRepository;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    private AuditorAware<String> auditorAware;

    @MockitoBean
    protected JwtTokenProvider provider;

    protected List<Poll> polls = new LinkedList<>();

    @BeforeEach
    void init(){
        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of("user"));
        polls.clear();
        pollRepository.deleteAll();

        List<Option> options = new LinkedList<>();

        for(int i=0;i<5;i++){
            Poll poll = new Poll();
            poll.setTitle("Test Poll" + i);
            poll.setDescription("Test Description" + i);
            poll.setCreatedAt(LocalDateTime.now());

            Option option = new Option();
            option.setText("Option"+i);
            option.setCreatedAt(LocalDateTime.now());
            option.setPoll(poll);
            options.add(option);
            poll.setOptions(options);

            polls.add(poll);
        }
        pollRepository.saveAll(polls);
    }

    protected String getToken() {
        return "Bearer " + provider.generateToken(new UsernamePasswordAuthenticationToken(
                SecurityContextHolder.getContext().getAuthentication().getName(),
                null,
                SecurityContextHolder.getContext().getAuthentication().getAuthorities()));
    }
}
