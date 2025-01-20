package com.aydnorcn.mis_app.integration;

import com.aydnorcn.mis_app.dto.poll.CreatePollRequest;
import com.aydnorcn.mis_app.dto.poll.PatchPollRequest;
import com.aydnorcn.mis_app.entity.Poll;
import com.aydnorcn.mis_app.integration.support.PollControllerIntegrationTestSupport;
import com.aydnorcn.mis_app.utils.MessageConstants;
import com.aydnorcn.mis_app.utils.PollType;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
class PollControllerIntegrationTest extends PollControllerIntegrationTestSupport {

    private final String API_URL = "/api/polls";

    @Test
    @WithMockUser
    void getPoll_ReturnPoll_WhenIdIsExists() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.get(API_URL + "/" + polls.get(0).getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(polls.get(0).getId()))
                .andExpect(jsonPath("$.title").value(polls.get(0).getTitle()))
                .andExpect(jsonPath("$.description").value(polls.get(0).getDescription()))
                .andDo(print());
    }

    @Test
    @WithMockUser
    void getPoll_ReturnNotFound_WhenIdIsNotExists() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.get(API_URL + "/123")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(MessageConstants.POLL_NOT_FOUND))
                .andDo(print());
    }

    @Test
    @WithAnonymousUser
    void getPoll_ReturnUnauthorized_WhenUserIsNotAuthenticated() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.get(API_URL + "/" + polls.get(0).getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(MessageConstants.AUTHENTICATION_REQUIRED))
                .andDo(print());
    }

    //TODO: Get Polls

    @Test
    @WithMockUser
    void getPolls_ReturnsPageResponseDto_WhenRequestIsValid() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get(API_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("sort-by", "name")
                        .header("Authorization", getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Test Poll0"))
                .andExpect(jsonPath("$.content[0].description").value("Test Description0"))
                .andExpect(jsonPath("$.content.size()").value(5))
                .andDo(print());
    }

    @Test
    @WithMockUser
    void getPolls_ReturnsEmptyPageResponseDto_WhenNoEventsFound() throws Exception {
        pollRepository.deleteAll();
        mockMvc.perform(MockMvcRequestBuilders.get(API_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(0))
                .andDo(print());
    }

    @Test
    @WithMockUser
    void getPolls_ReturnsDefaultPageResponseDto_WhenParamsAreInvalid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(API_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("sort-by", "invalid")
                        .header("Authorization", getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(5))
                .andDo(print());
    }

    @Test
    @WithMockUser
    void getPolls_ReturnsFilteredEvents_WhenValidFilterParamsProvided() throws Exception{
        Poll poll = new Poll();
        poll.setTitle("Filtered Poll");
        poll.setType(PollType.MULTIPLE_CHOICE);
        poll.setCreatedAt(LocalDateTime.now());
        pollRepository.save(poll);

        mockMvc
                .perform(MockMvcRequestBuilders.get(API_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("poll-type", "multiple")
                        .header("Authorization", getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Filtered Poll"))
                .andExpect(jsonPath("$.content.size()").value(1))
                .andDo(print());
    }

    @Test
    @WithMockUser
    void getPolls_ReturnsSortedPolls_WhenValidSortParamsProvided() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.get(API_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("sort-by", "title")
                        .header("Authorization", getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Test Poll0"))
                .andExpect(jsonPath("$.content[1].title").value("Test Poll1"))
                .andExpect(jsonPath("$.content[2].title").value("Test Poll2"))
                .andExpect(jsonPath("$.content[3].title").value("Test Poll3"))
                .andExpect(jsonPath("$.content[4].title").value("Test Poll4"))
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createPoll_ReturnsCreatedPoll_WhenParamsAreValidAndUserAdmin() throws Exception {
        CreatePollRequest request = new CreatePollRequest("Test Poll", "Test Description", PollType.SINGLE_CHOICE, List.of("Option1", "Option2"), 1);

        mockMvc
                .perform(MockMvcRequestBuilders.post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(request.getTitle()))
                .andExpect(jsonPath("$.description").value(request.getDescription()))
                .andDo(print());
    }

    @Test
    @WithMockUser
    void createPoll_ReturnUnauthorized_WhenUserIsNotAdmin() throws Exception {
        CreatePollRequest request = new CreatePollRequest("Test Poll", "Test Description", PollType.SINGLE_CHOICE, List.of("Option1", "Option2"), 1);

        mockMvc
                .perform(MockMvcRequestBuilders.post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(MessageConstants.UNAUTHORIZED_ACTION))
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createPoll_ReturnBadRequest_WhenParamsAreNotValid() throws Exception {
        CreatePollRequest request = new CreatePollRequest(null, "", PollType.SINGLE_CHOICE, List.of("Option1", "Option2"), 1);

        mockMvc
                .perform(MockMvcRequestBuilders.post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value(MessageConstants.TITLE_NOT_BLANK))
                .andExpect(jsonPath("$.description").value(MessageConstants.DESCRIPTION_NOT_BLANK))
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updatePoll_ReturnsUpdatedPoll_WhenParamsAreValidAndUserIsAdmin() throws Exception {
        CreatePollRequest request = new CreatePollRequest("Updated Title", "Updated Description", PollType.SINGLE_CHOICE, List.of("Option1", "Option2"), 1);

        mockMvc
                .perform(MockMvcRequestBuilders.put(API_URL + "/" + polls.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(request.getTitle()))
                .andExpect(jsonPath("$.description").value(request.getDescription()))
                .andDo(print());
    }

    @Test
    @WithMockUser
    void updatePoll_ReturnsUnauthorized_WhenUserIsNotAdmin() throws Exception {
        CreatePollRequest request = new CreatePollRequest("Updated Title", "Updated Description", PollType.SINGLE_CHOICE, List.of("Option1", "Option2"), 1);

        mockMvc
                .perform(MockMvcRequestBuilders.put(API_URL + "/" + polls.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(MessageConstants.UNAUTHORIZED_ACTION))
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updatePoll_ReturnsBadRequest_WhenParamsAreNotValid() throws Exception {
        CreatePollRequest request = new CreatePollRequest(null, null, PollType.SINGLE_CHOICE, List.of("Option1", "Option2"), 1);

        mockMvc
                .perform(MockMvcRequestBuilders.put(API_URL + "/" + polls.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value(MessageConstants.TITLE_NOT_BLANK))
                .andExpect(jsonPath("$.description").value(MessageConstants.DESCRIPTION_NOT_BLANK))
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updatePoll_ReturnsNotFound_WhenPollIdDoesNotExists() throws Exception {
        CreatePollRequest request = new CreatePollRequest("Updated Title", "Updated Description", PollType.SINGLE_CHOICE, List.of("Option1", "Option2"), 1);

        mockMvc
                .perform(MockMvcRequestBuilders.put(API_URL + "/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(MessageConstants.POLL_NOT_FOUND))
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updatePoll_ReturnsBadRequest_WhenRequestBodyIsEmpty() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.put(API_URL + "/" + polls.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(MessageConstants.REQUEST_BODY_MISSING))
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void patchPoll_ReturnsUpdatedPoll_WhenParamsAreValidAndUserIsAdmin() throws Exception {
        PatchPollRequest request = new PatchPollRequest("Patched Title", null, null,null);

        mockMvc
                .perform(MockMvcRequestBuilders.patch(API_URL + "/" + polls.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(request.getTitle()))
                .andExpect(jsonPath("$.description").value(polls.get(0).getDescription()))
                .andDo(print());
    }

    @Test
    @WithMockUser
    void patchPoll_ReturnsUnauthorized_WhenUserIsNotAdmin() throws Exception {
        PatchPollRequest request = new PatchPollRequest("Patched Title", null, null,null);

        mockMvc
                .perform(MockMvcRequestBuilders.patch(API_URL + "/" + polls.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(MessageConstants.UNAUTHORIZED_ACTION))
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void patchPoll_ReturnsPoll_WhenAllParamsAreNull() throws Exception {
        PatchPollRequest request = new PatchPollRequest(null, null, null,null);

        mockMvc
                .perform(MockMvcRequestBuilders.patch(API_URL + "/" + polls.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(polls.get(0).getTitle()))
                .andExpect(jsonPath("$.description").value(polls.get(0).getDescription()))
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void patchPoll_ReturnsNotFound_WhenPollIdDoesNotExists() throws Exception {
        PatchPollRequest request = new PatchPollRequest("Patched Title", null, null,null);

        mockMvc
                .perform(MockMvcRequestBuilders.patch(API_URL + "/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(MessageConstants.POLL_NOT_FOUND))
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void patchPoll_ReturnsBadRequest_WhenRequestBodyIsEmpty() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.patch(API_URL + "/" + polls.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(MessageConstants.REQUEST_BODY_MISSING))
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePoll_ReturnsNoContent_WhenPollIdExists() throws Exception {
        String token = getToken();

        mockMvc
                .perform(MockMvcRequestBuilders.delete(API_URL + "/" + polls.get(0).getId())
                        .header("Authorization", token))
                .andExpect(status().isNoContent());

        mockMvc.perform(MockMvcRequestBuilders.get(API_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(4))
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePoll_ReturnsNotFound_WhenPollIdDoesNotExists() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.delete(API_URL + "/123")
                        .header("Authorization", getToken()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(MessageConstants.POLL_NOT_FOUND))
                .andDo(print());
    }

    @Test
    @WithMockUser
    void deletePoll_ReturnsUnauthorized_WhenUserIsNotAdmin() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.delete(API_URL + "/" + polls.get(0).getId())
                        .header("Authorization", getToken()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(MessageConstants.UNAUTHORIZED_ACTION))
                .andDo(print());
    }
}
