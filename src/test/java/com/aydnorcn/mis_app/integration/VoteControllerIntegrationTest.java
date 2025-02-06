package com.aydnorcn.mis_app.integration;

import com.aydnorcn.mis_app.dto.vote.VoteRequest;
import com.aydnorcn.mis_app.entity.Poll;
import com.aydnorcn.mis_app.integration.support.VoteControllerIntegrationTestSupport;
import com.aydnorcn.mis_app.utils.PollType;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
class VoteControllerIntegrationTest extends VoteControllerIntegrationTestSupport {

    private final String API_URL = "/api/votes";

    @Test
    void getVote_ShouldReturnVoteResponse_WhenVoteExists() throws Exception {
        String token = getToken(user_email, password);
        String id = createVote(options.get(0), user_email);

        mockMvc.perform(MockMvcRequestBuilders.get(API_URL + '/' + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token))
                .andExpect(status().isOk());
    }

    @Test
    void getVote_ShouldReturnNotFound_WhenVoteDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getVotes_ShouldReturnPageResponseDto_WhenParamsAreValid() throws Exception {
        createVote(options.get(0), user_email);

        mockMvc.perform(MockMvcRequestBuilders.get(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password))
                        .param("option-id", options.get(0).getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(votes.size() + 1));
    }


    @Test
    void getVotes_ShouldReturnDefaultPageResponseDto_WhenNoVotesExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(votes.size()));
    }

    @Test
    void createVote_ShouldReturnCreatedVoteResponse_WhenRequestIsValid() throws Exception {
        VoteRequest request = new VoteRequest();
        request.setOptionId(options.get(0).getId());

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void createVote_ShouldDeleteExistingVoteAndReturnCreatedVoteResponse_WhenPollTypeIsSingleChoiceAndRequestIsValid() throws Exception {
        VoteRequest request = new VoteRequest();
        request.setOptionId(options.get(1).getId());

        createVote(options.get(0), user_email);

        String token = getToken(user_email, password);

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.optionId").value(options.get(1).getId()))
                .andExpect(jsonPath("$.pollId").value(options.get(1).getPoll().getId()));

        mockMvc.perform(MockMvcRequestBuilders.get(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .param("option-id", options.get(0).getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(0));
    }

    @Test
    void createVote_ShouldVoteMultipleOptionsAndReturnCreatedVoteResponse_WhenPollTypeIsMultipleChoiceAndRequestIsValid() throws Exception {
        Poll multipleChoicePoll = options.stream().filter(x -> x.getPoll().getType().equals(PollType.MULTIPLE_CHOICE)).findFirst().get().getPoll();

        createVote(multipleChoicePoll.getOptions().get(0), user_email);

        VoteRequest request = new VoteRequest();
        request.setOptionId(multipleChoicePoll.getOptions().get(1).getId());

        String token = getToken(user_email, password);

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders.get(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .param("poll-id", multipleChoicePoll.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(2));
    }

    @Test
    void createVote_ShouldReturnBadRequest_WhenRequestIsInvalid() throws Exception {
        VoteRequest request = new VoteRequest();
        request.setOptionId(null);

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteVote_ShouldDeleteVote_WhenVoteExistsAndUserIsAuthorized() throws Exception {
        String id = createVote(options.get(0), user_email);

        mockMvc.perform(MockMvcRequestBuilders.delete(API_URL + '/' + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteVote_ShouldReturnForbidden_WhenUserIsNotAuthorized() throws Exception {
        String id = createVote(options.get(0), "user10@mail.com");

        mockMvc.perform(MockMvcRequestBuilders.delete(API_URL + '/' + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteVote_ShouldReturnNotFound_WhenVoteDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(API_URL + "/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isNotFound());
    }
}
