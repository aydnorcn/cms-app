package com.aydnorcn.mis_app.filter;

import com.aydnorcn.mis_app.dto.auth.LoginRequest;
import com.aydnorcn.mis_app.dto.auth.RegisterRequest;
import com.aydnorcn.mis_app.exception.APIException;
import com.aydnorcn.mis_app.jwt.JwtTokenProvider;
import com.aydnorcn.mis_app.repository.UserRepository;
import com.aydnorcn.mis_app.utils.MessageConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JwtAuthenticationFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtTokenProvider tokenProvider;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @Autowired
    private ObjectMapper objectMapper;

    private final String validToken = "valid.jwt.token";
    private final String invalidToken = "invalid.jwt.token";
    private final String testMail = "mail@mail.com";
    private final String password = "password123";

    @BeforeEach
    void setup() {
        UserDetails userDetails = User.builder()
                .username(testMail)
                .password(password)
                .authorities("ROLE_USER")
                .build();

        when(tokenProvider.validateToken(validToken)).thenReturn(true);
        when(tokenProvider.validateToken(invalidToken)).thenThrow(new APIException(HttpStatus.BAD_REQUEST, MessageConstants.INVALID_JWT_TOKEN));
        when(tokenProvider.getUsername(validToken)).thenReturn(testMail);
        when(userDetailsService.loadUserByUsername(testMail)).thenReturn(userDetails);
    }

    @Test
    void shouldAllowAccessToLoginEndpoint_WhenTokenIsNotExists() throws Exception {
        String generatedToken = "generated.jwt.token";

        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(testMail, password)))
                .thenReturn(new UsernamePasswordAuthenticationToken(testMail, password));

        when(tokenProvider.generateToken(new UsernamePasswordAuthenticationToken(testMail, password)))
                .thenReturn(generatedToken);

        LoginRequest loginRequest = new LoginRequest(testMail, password);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(testMail))
                .andExpect(jsonPath("$.token").value(generatedToken))
                .andDo(print());
    }

    @Test
    void shouldAllowAccessToRegisterEndpoint_WhenTokenIsNotExists() throws Exception {
        RegisterRequest request = new RegisterRequest(testMail, password, "firstName", "lastName");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void shouldReturnUnauthorized_WhenTokenIsMissing() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/protected-endpoint"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(MessageConstants.AUTHENTICATION_REQUIRED))
                .andDo(print());
    }

    @Test
    void shouldAuthenticateSuccessfully_WhenTokenIsValid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/events")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @WithMockUser
    void shouldReturnBadRequest_WhenTokenIsInvalid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/events")
                        .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(MessageConstants.INVALID_JWT_TOKEN))
                .andDo(print());
    }
}
