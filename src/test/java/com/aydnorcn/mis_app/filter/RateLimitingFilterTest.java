package com.aydnorcn.mis_app.filter;

import com.aydnorcn.mis_app.jwt.JwtTokenProvider;
import com.aydnorcn.mis_app.utils.MessageConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RateLimitingFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    protected JwtTokenProvider provider;

    protected String getToken() {
        return "Bearer " + provider.generateToken(new UsernamePasswordAuthenticationToken(
                SecurityContextHolder.getContext().getAuthentication().getName(),
                null,
                SecurityContextHolder.getContext().getAuthentication().getAuthorities()));
    }

    @Test
    @WithMockUser
    void filter_ReturnTooManyRequest_WhenExceedRateLimit() throws Exception {
        String token = getToken();
        for (int i = 0; i < 50; i++) {
            mockMvc.perform(MockMvcRequestBuilders.get("/api/events")
                    .header("Authorization", token));
        }

        mockMvc.perform(MockMvcRequestBuilders.get("/api/events")
                        .header("Authorization", token))
                .andExpect(MockMvcResultMatchers.status().isTooManyRequests())
                .andExpect(jsonPath("$.message").value(MessageConstants.RATE_LIMIT_EXCEEDED))
                .andDo(print());
    }

    @Test
    @WithMockUser
    void filter_ReturnResponse_WhenUnderLimit() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/events")
                        .header("Authorization", getToken()))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Rate-Limit-Remaining"))
                .andDo(print());
    }
}
