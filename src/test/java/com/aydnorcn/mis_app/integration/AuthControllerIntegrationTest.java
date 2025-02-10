package com.aydnorcn.mis_app.integration;

import com.aydnorcn.mis_app.dto.auth.LoginRequest;
import com.aydnorcn.mis_app.dto.auth.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.aydnorcn.mis_app.utils.MessageConstants.PASSWORD_LENGTH;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String API_URL = "/api/auth";


    @Test
    void register_ReturnRegisterResponse_WhenBodyIsValid() throws Exception {
        String mail = "valid-mail1@mail.com";
        RegisterRequest request = new RegisterRequest(mail, "password123", "firstName", "lastName");

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL + '/' + "register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(mail))
                .andExpect(jsonPath("$.firstName").value("firstName"))
                .andExpect(jsonPath("$.lastName").value("lastName"));
    }

    @Test
    void register_ReturnsBadRequest_WhenPasswordIsTooShort() throws Exception {
        String mail = "valid-mail@mail.com";
        RegisterRequest request = new RegisterRequest(mail, "pass", "firstName", "lastName");

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL + '/' + "register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data..password").value(PASSWORD_LENGTH));
    }

    @Test
    void register_ReturnsBadRequest_WhenFirstNameIsMissing() throws Exception {
        String mail = "valid-mail@mail.com";
        RegisterRequest request = new RegisterRequest(mail, "password123", null, "lastName");

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL + '/' + "register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_ReturnsBadRequest_WhenLastNameIsMissing() throws Exception {
        String mail = "valid-mail@mail.com";
        RegisterRequest request = new RegisterRequest(mail, "password123", "firstName", null);

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL + '/' + "register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_ReturnsBadRequest_WhenEmailIsInvalid() throws Exception {
        String mail = "not-valid-mail";
        RegisterRequest request = new RegisterRequest(mail, "password123", "firstName", "lastName");

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL + '/' + "register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_ReturnsConflict_WhenEmailAlreadyExists() throws Exception {
        String mail = "valid-mail@mail.com";
        RegisterRequest request = new RegisterRequest(mail, "password123", "firstName", "lastName");

        registerUser();

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL + '/' + "register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }


    @Test
    void login_ReturnLoginResponse_WhenBodyIsValid() throws Exception {
        String mail = "valid-mail@mail.com";
        LoginRequest request = new LoginRequest(mail, "password123");

        registerUser();

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL + "/" + "login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(mail))
                .andExpect(jsonPath("$.accessToken").exists());
    }

    @Test
    void login_ReturnsUnauthorized_WhenEmailIsInvalid() throws Exception {
        String mail = "invalid-mail@mail.com";
        LoginRequest request = new LoginRequest(mail, "password123");

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL + "/" + "login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_ReturnsUnauthorized_WhenPasswordIsIncorrect() throws Exception {
        String mail = "valid-mail@mail.com";
        LoginRequest request = new LoginRequest(mail, "password123456");

        registerUser();

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL + "/" + "login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_ReturnsBadRequest_WhenEmailIsMissing() throws Exception {
        LoginRequest request = new LoginRequest(null, "password123");

        registerUser();

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL + "/" + "login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_ReturnsBadRequest_WhenPasswordIsMissing() throws Exception {
        String mail = "valid-mail@mail.com";
        LoginRequest request = new LoginRequest(mail, null);

        registerUser();

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL + "/" + "login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }


    private void registerUser() throws Exception {
        String mail = "valid-mail@mail.com";
        RegisterRequest request = new RegisterRequest(mail, "password123", "firstName", "lastName");

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL + '/' + "register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }
}