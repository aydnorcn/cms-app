package com.aydnorcn.mis_app.integration;

import com.aydnorcn.mis_app.dto.poll.CreatePollRequest;
import com.aydnorcn.mis_app.dto.poll.PatchPollRequest;
import com.aydnorcn.mis_app.integration.support.PollControllerIntegrationTestSupport;
import com.aydnorcn.mis_app.utils.MessageConstants;
import com.aydnorcn.mis_app.utils.PollType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@Transactional
class PollControllerIntegrationTest extends PollControllerIntegrationTestSupport {

    private final String API_URL = "/api/polls";

    @Test
    void getPoll_ReturnPoll_WhenIdIsExists() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.get(API_URL + "/" + polls.get(0).getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(polls.get(0).getId()))
                .andExpect(jsonPath("$.title").value(polls.get(0).getTitle()))
                .andExpect(jsonPath("$.description").value(polls.get(0).getDescription()))
                .andDo(print());
    }

    @Test
    void getPoll_ReturnNotFound_WhenIdIsNotExists() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.get(API_URL + "/123")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(MessageConstants.POLL_NOT_FOUND))
                .andDo(print());
    }

    @Test
    void getPoll_ReturnUnauthorized_WhenUserIsNotAuthenticated() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.get(API_URL + "/" + polls.get(0).getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(MessageConstants.AUTHENTICATION_REQUIRED))
                .andDo(print());
    }

    @Test
    void getPolls_ReturnsPageResponseDto_WhenRequestIsValid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(API_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("sort-by", "name")
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Title 1"))
                .andExpect(jsonPath("$.content[0].description").value("Description 1"))
                .andExpect(jsonPath("$.content.size()").value(10))
                .andDo(print());
    }

    @Test
    void getPolls_ReturnsEmptyPageResponseDto_WhenNoEventsFound() throws Exception {
        pollRepository.deleteAll();
        mockMvc.perform(MockMvcRequestBuilders.get(API_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(0))
                .andDo(print());
    }

    @Test
    void getPolls_ReturnsDefaultPageResponseDto_WhenParamsAreInvalid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(API_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("sort-by", "invalid")
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(10))
                .andDo(print());
    }

    @Test
    void getPolls_ReturnsFilteredEvents_WhenValidFilterParamsProvided() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.get(API_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("poll-type", "multiple")
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Title 2"))
                .andExpect(jsonPath("$.content.size()").value(5))
                .andDo(print());
    }

    @Test
    void getPolls_ReturnsSortedPolls_WhenValidSortParamsProvided() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.get(API_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("sort-by", "title")
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Title 1"))
                .andExpect(jsonPath("$.content[9].title").value("Title 10"))
                .andDo(print());
    }

    @Test
    void createPoll_ReturnsCreatedPoll_WhenParamsAreValidAndUserAdmin() throws Exception {
        CreatePollRequest request = new CreatePollRequest("Test Poll", "Test Description", PollType.SINGLE_CHOICE, List.of("Option1", "Option2"), 1);

        mockMvc
                .perform(MockMvcRequestBuilders.post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(request.getTitle()))
                .andExpect(jsonPath("$.description").value(request.getDescription()))
                .andDo(print());
    }

    @Test
    void createPoll_ReturnUnauthorized_WhenUserIsNotAdmin() throws Exception {
        CreatePollRequest request = new CreatePollRequest("Test Poll", "Test Description", PollType.SINGLE_CHOICE, List.of("Option1", "Option2"), 1);

        mockMvc
                .perform(MockMvcRequestBuilders.post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(MessageConstants.UNAUTHORIZED_ACTION))
                .andDo(print());
    }

    @Test
    void createPoll_ReturnBadRequest_WhenParamsAreNotValid() throws Exception {
        CreatePollRequest request = new CreatePollRequest(null, "", PollType.SINGLE_CHOICE, List.of("Option1", "Option2"), 1);

        mockMvc
                .perform(MockMvcRequestBuilders.post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value(MessageConstants.TITLE_NOT_BLANK))
                .andExpect(jsonPath("$.description").value(MessageConstants.DESCRIPTION_NOT_BLANK))
                .andDo(print());
    }

    @Test
    void updatePoll_ReturnsUpdatedPoll_WhenParamsAreValidAndUserIsAdmin() throws Exception {
        CreatePollRequest request = new CreatePollRequest("Updated Title", "Updated Description", PollType.SINGLE_CHOICE, List.of("Option1", "Option2"), 1);

        mockMvc
                .perform(MockMvcRequestBuilders.put(API_URL + "/" + polls.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(request.getTitle()))
                .andExpect(jsonPath("$.description").value(request.getDescription()))
                .andDo(print());
    }

    @Test
    void updatePoll_ReturnsUnauthorized_WhenUserIsNotAdmin() throws Exception {
        CreatePollRequest request = new CreatePollRequest("Updated Title", "Updated Description", PollType.SINGLE_CHOICE, List.of("Option1", "Option2"), 1);

        mockMvc
                .perform(MockMvcRequestBuilders.put(API_URL + "/" + polls.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(MessageConstants.UNAUTHORIZED_ACTION))
                .andDo(print());
    }

    @Test
    void updatePoll_ReturnsBadRequest_WhenParamsAreNotValid() throws Exception {
        CreatePollRequest request = new CreatePollRequest(null, null, PollType.SINGLE_CHOICE, List.of("Option1", "Option2"), 1);

        mockMvc
                .perform(MockMvcRequestBuilders.put(API_URL + "/" + polls.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value(MessageConstants.TITLE_NOT_BLANK))
                .andExpect(jsonPath("$.description").value(MessageConstants.DESCRIPTION_NOT_BLANK))
                .andDo(print());
    }

    @Test
    void updatePoll_ReturnsNotFound_WhenPollIdDoesNotExists() throws Exception {
        CreatePollRequest request = new CreatePollRequest("Updated Title", "Updated Description", PollType.SINGLE_CHOICE, List.of("Option1", "Option2"), 1);

        mockMvc
                .perform(MockMvcRequestBuilders.put(API_URL + "/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(MessageConstants.POLL_NOT_FOUND))
                .andDo(print());
    }

    @Test
    void updatePoll_ReturnsBadRequest_WhenRequestBodyIsEmpty() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.put(API_URL + "/" + polls.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(MessageConstants.REQUEST_BODY_MISSING))
                .andDo(print());
    }

    @Test
    void patchPoll_ReturnsUpdatedPoll_WhenParamsAreValidAndUserIsAdmin() throws Exception {
        PatchPollRequest request = new PatchPollRequest("Patched Title", null, null, null);

        mockMvc
                .perform(MockMvcRequestBuilders.patch(API_URL + "/" + polls.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(request.getTitle()))
                .andExpect(jsonPath("$.description").value(polls.get(0).getDescription()))
                .andDo(print());
    }

    @Test
    void patchPoll_ReturnsUnauthorized_WhenUserIsNotAdmin() throws Exception {
        PatchPollRequest request = new PatchPollRequest("Patched Title", null, null, null);

        mockMvc
                .perform(MockMvcRequestBuilders.patch(API_URL + "/" + polls.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(MessageConstants.UNAUTHORIZED_ACTION))
                .andDo(print());
    }

    @Test
    void patchPoll_ReturnsPoll_WhenAllParamsAreNull() throws Exception {
        PatchPollRequest request = new PatchPollRequest(null, null, null, null);

        mockMvc
                .perform(MockMvcRequestBuilders.patch(API_URL + "/" + polls.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(polls.get(0).getTitle()))
                .andExpect(jsonPath("$.description").value(polls.get(0).getDescription()))
                .andDo(print());
    }

    @Test
    void patchPoll_ReturnsNotFound_WhenPollIdDoesNotExists() throws Exception {
        PatchPollRequest request = new PatchPollRequest("Patched Title", null, null, null);

        mockMvc
                .perform(MockMvcRequestBuilders.patch(API_URL + "/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(MessageConstants.POLL_NOT_FOUND))
                .andDo(print());
    }

    @Test
    void patchPoll_ReturnsBadRequest_WhenRequestBodyIsEmpty() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.patch(API_URL + "/" + polls.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(MessageConstants.REQUEST_BODY_MISSING))
                .andDo(print());
    }

    @Test
    void deletePoll_ReturnsNoContent_WhenPollIdExists() throws Exception {
        String token = getToken(admin_email, password);

        mockMvc
                .perform(MockMvcRequestBuilders.delete(API_URL + "/" + polls.get(0).getId())
                        .header("Authorization", token))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void deletePoll_ReturnsNotFound_WhenPollIdDoesNotExists() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.delete(API_URL + "/123")
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(MessageConstants.POLL_NOT_FOUND))
                .andDo(print());
    }

    @Test
    void deletePoll_ReturnsUnauthorized_WhenUserIsNotAdmin() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.delete(API_URL + "/" + polls.get(0).getId())
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(MessageConstants.UNAUTHORIZED_ACTION))
                .andDo(print());
    }
}
