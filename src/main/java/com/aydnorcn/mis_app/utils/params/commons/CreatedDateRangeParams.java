package com.aydnorcn.mis_app.utils.params.commons;

import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Getter
public class CreatedDateRangeParams {

    @DateTimeFormat(pattern = "yyyy.MM.dd HH:mm")
    private LocalDateTime createdAfter = null;

    @DateTimeFormat(pattern = "yyyy.MM.dd HH:mm")
    private LocalDateTime createdBefore = null;

    public CreatedDateRangeParams(Map<String, Object> params) {
        if (params.containsKey("created-after"))
            createdAfter = LocalDateTime.parse((String) params.get("created-after"), DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
        if (params.containsKey("created-before"))
            createdBefore = LocalDateTime.parse((String) params.get("created-before"), DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
    }
}
