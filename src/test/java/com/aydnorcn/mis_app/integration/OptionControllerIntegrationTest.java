package com.aydnorcn.mis_app.integration;

import com.aydnorcn.mis_app.dto.option.CreateOptionRequest;
import com.aydnorcn.mis_app.dto.option.UpdateOptionRequest;
import com.aydnorcn.mis_app.entity.Option;
import com.aydnorcn.mis_app.integration.support.OptionControllerIntegrationTestSupport;
import com.aydnorcn.mis_app.utils.MessageConstants;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
class OptionControllerIntegrationTest extends OptionControllerIntegrationTestSupport {

    private final String API_URL = "/api/options";

    @Test
    void getOptionById_ReturnsOption_WhenOptionExists() throws Exception{
        Option option = options.get(0);

        mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/" + option.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(option.getId()))
                .andExpect(jsonPath("$.data.text").value(option.getText()));
    }

    @Test
    void getOptionById_ReturnsForbidden_WhenUserIsNotAuthenticated() throws Exception{
        Option option = options.get(0);

        mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/" + option.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getOptionById_ReturnsNotFound_WhenOptionDoesNotExist() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/not-exist-id")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createOption_ReturnsOption_WhenRequestIsValid() throws Exception{
        CreateOptionRequest request = new CreateOptionRequest();
        request.setOptionText("New Option");
        request.setPollId(options.get(0).getPoll().getId());

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.text").value("New Option"));
    }

    @Test
    void createOption_ReturnsForbidden_WhenUserIsNotPermitted() throws Exception{
        CreateOptionRequest request = new CreateOptionRequest();
        request.setOptionText("New Option");
        request.setPollId(options.get(0).getPoll().getId());

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createOption_ReturnsNotFound_WhenPollIdIsNotValid() throws Exception{
        CreateOptionRequest request = new CreateOptionRequest();
        request.setOptionText("New Option");
        request.setPollId("not-exist-id");

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createOption_ReturnsBadRequest_WhenOptionTextIsNotValid() throws Exception{
        CreateOptionRequest request = new CreateOptionRequest();
        request.setOptionText("");
        request.setPollId(options.get(0).getPoll().getId());

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateOption_ReturnsOption_WhenRequestIsValid() throws Exception{
        UpdateOptionRequest request = new UpdateOptionRequest();
        request.setOptionText("New Option Text");

        Option option = options.get(0);

        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/" + option.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.text").value(request.getOptionText()));
    }

    @Test
    void updateOption_ReturnsForbidden_WhenUserIsNotPermitted() throws Exception{
        UpdateOptionRequest request = new UpdateOptionRequest();
        request.setOptionText("New Option Text");

        Option option = options.get(0);

        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/" + option.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateOption_ReturnsNotFound_WhenOptionDoesNotExist() throws Exception{
        UpdateOptionRequest request = new UpdateOptionRequest();
        request.setOptionText("New Option Text");

        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/" + "not-exist-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateOption_ReturnsBadRequest_WhenOptionTextIsNotValid() throws Exception{
        UpdateOptionRequest request = new UpdateOptionRequest();
        request.setOptionText("");


        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/" + options.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.message.optionText").value(MessageConstants.OPTION_TEXT_NOT_BLANK));
    }

    @Test
    void deleteOption_ReturnsNoContent_WhenOptionExists() throws Exception{
        Option option = options.get(0);

        mockMvc.perform(MockMvcRequestBuilders.delete(API_URL + "/" + option.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteOption_ReturnsForbidden_WhenUserIsNotPermitted() throws Exception{
        Option option = options.get(0);

        mockMvc.perform(MockMvcRequestBuilders.delete(API_URL + "/" + option.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteOption_ReturnsNotFound_WhenOptionDoesNotExist() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.delete(API_URL + "/not-exist-id")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isNotFound());
    }
}
