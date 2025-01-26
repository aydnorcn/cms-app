package com.aydnorcn.mis_app;

import com.aydnorcn.mis_app.dto.auth.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class TestUtils {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Value("${test.login.email}")
    protected String user_email;

    @Value("${test.login.admin-email}")
    protected String admin_email;

    @Value("${test.login.password}")
    protected String password;

    private static String token;
    private static String last_login_email;

    protected String getToken(String email, String password) throws Exception {
        if (last_login_email != null && last_login_email.equals(email) && token != null) {
            return String.format("Bearer %s", token);
        }

        LoginRequest request = new LoginRequest(email, password);

        String response = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        last_login_email = email;
        token = JsonPath.read(response, "$.token");
        return String.format("Bearer %s", token);
    }
}
