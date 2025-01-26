package com.aydnorcn.mis_app.integration.support;

import com.aydnorcn.mis_app.TestUtils;
import com.aydnorcn.mis_app.dto.vote.VoteRequest;
import com.aydnorcn.mis_app.entity.Option;
import com.aydnorcn.mis_app.entity.Vote;
import com.aydnorcn.mis_app.repository.PollRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.AuditorAware;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class VoteControllerIntegrationTestSupport extends TestUtils {

    @MockitoBean
    private AuditorAware<String> auditorAware;

    @Autowired
    private PollRepository pollRepository;

    protected static List<Option> options;


    @BeforeEach
    void init() {
        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of("user"));

        options = pollRepository.findAll().stream()
                .flatMap(p -> p.getOptions().stream())
                .toList();
    }

    protected Vote createVote(Option option, String email) throws Exception {
        VoteRequest request = new VoteRequest();
        request.setOptionId(option.getId());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/votes")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(email, password)))
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsString(), Vote.class);
    }
}