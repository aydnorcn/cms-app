package com.aydnorcn.mis_app.security;

import com.aydnorcn.mis_app.exception.ErrorMessage;
import com.aydnorcn.mis_app.exception.RateLimitExceedException;
import com.aydnorcn.mis_app.utils.MessageConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RateLimitingFilter extends OncePerRequestFilter {
    private final Map<String, Bucket> buckets = new HashMap<>();

    private final int REQUESTS_PER_MINUTE;

    public RateLimitingFilter(int requestPerMinute) {
        this.REQUESTS_PER_MINUTE = requestPerMinute;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String ipAddress = request.getRemoteAddr();
        Bucket bucket = buckets.computeIfAbsent(ipAddress,
                ip -> Bucket.builder()
                        .addLimit(limit -> limit.capacity(REQUESTS_PER_MINUTE).refillGreedy(REQUESTS_PER_MINUTE, Duration.ofMinutes(1)))
                        .build());

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        try {
            if (!probe.isConsumed()) throw new RateLimitExceedException(MessageConstants.RATE_LIMIT_EXCEEDED);
            response.addHeader("X-Rate-Limit-Remaining", Long.toString(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
        } catch (RateLimitExceedException e) {
            ErrorMessage errorMessage = new ErrorMessage(new Date(), e.getMessage());

            ObjectMapper mapper = new ObjectMapper();
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));

            response.setContentType("application/json");
            response.setStatus(429);

            PrintWriter out = response.getWriter();
            out.write(mapper.writeValueAsString(errorMessage));
            out.flush();
        }
    }
}
