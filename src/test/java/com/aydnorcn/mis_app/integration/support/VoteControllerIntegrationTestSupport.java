package com.aydnorcn.mis_app.integration.support;

import com.aydnorcn.mis_app.TestUtils;
import com.aydnorcn.mis_app.dto.vote.VoteRequest;
import com.aydnorcn.mis_app.entity.Option;
import com.aydnorcn.mis_app.entity.Vote;
import com.aydnorcn.mis_app.repository.PollRepository;
import com.aydnorcn.mis_app.repository.VoteRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class VoteControllerIntegrationTestSupport extends TestUtils {
    @Autowired
    private PollRepository pollRepository;

    @Autowired
    protected VoteRepository voteRepository;

    protected static List<Option> options;
    protected static List<Vote> votes;

    @BeforeEach
    void init() {
        options = pollRepository.findAll().stream()
                .flatMap(p -> p.getOptions().stream())
                .toList();

        votes = voteRepository.findAll();
    }

    protected String createVote(Option option, String email) throws Exception {
        VoteRequest request = new VoteRequest();
        request.setOptionId(option.getId());

        String response = mockMvc.perform(MockMvcRequestBuilders.post("/api/votes")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(email, password)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return JsonPath.read(response, "$.id");
    }
}