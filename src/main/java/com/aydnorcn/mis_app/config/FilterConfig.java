package com.aydnorcn.mis_app.config;

import com.aydnorcn.mis_app.security.RateLimitingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
public class FilterConfig {

    @Value("${rate.limiting.max.requests-per-minute}")
    private int maxRequestPerMinute;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver exceptionResolver;

    @Bean
    public FilterRegistrationBean<RateLimitingFilter> rateLimitFilter() {
        FilterRegistrationBean<RateLimitingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RateLimitingFilter(maxRequestPerMinute, exceptionResolver));
        registrationBean.addUrlPatterns("/api/*");
        return registrationBean;
    }
}
