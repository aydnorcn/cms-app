package com.aydnorcn.mis_app.config;

import com.aydnorcn.mis_app.security.RateLimitingFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Value("${rate.limiting.max.requests-per-minute}")
    private int maxRequestPerMinute;

    @Bean
    public FilterRegistrationBean<RateLimitingFilter> rateLimitFilter() {
        FilterRegistrationBean<RateLimitingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RateLimitingFilter(maxRequestPerMinute));
        registrationBean.addUrlPatterns("/api/*");
        return registrationBean;
    }
}
