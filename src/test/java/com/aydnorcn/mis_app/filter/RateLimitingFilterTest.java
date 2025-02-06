package com.aydnorcn.mis_app.filter;

import com.aydnorcn.mis_app.TestUtils;
import com.aydnorcn.mis_app.utils.MessageConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RateLimitingFilterTest extends TestUtils {

    @Value("${rate.limiting.max.requests-per-minute}")
    protected int maxRequestsPerMinute;


    @Test
    void filter_ReturnTooManyRequest_WhenExceedRateLimit() throws Exception {
        String token = getToken(user_email, password);
        for (int i = 0; i < maxRequestsPerMinute * 2; i++) {
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
    void filter_ReturnResponse_WhenUnderLimit() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/events")
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Rate-Limit-Remaining"))
                .andDo(print());
    }
}
