package com.aydnorcn.mis_app.utils.params;

import com.aydnorcn.mis_app.utils.PollType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
public class PollParams {

    private PollType type = null;
    private Integer minOptionCount = null;
    private Integer maxOptionCount = null;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAfter = null;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdBefore = null;
    
    private String createdBy = null;
    private int pageNo = 0;
    private int pageSize = 10;

    public PollParams(Map<String, Object> params) {
        if (params.containsKey("poll-type")) type = PollType.fromString((String) params.get("poll-type"));
        if (params.containsKey("min-option-count"))
            minOptionCount = Integer.parseInt((String) params.get("min-option-count"));
        if (params.containsKey("max-option-count"))
            maxOptionCount = Integer.parseInt((String) params.get("max-option-count"));
        if (params.containsKey("created-after"))
            createdAfter = LocalDateTime.parse((String) params.get("created-after"));
        if (params.containsKey("created-before"))
            createdBefore = LocalDateTime.parse((String) params.get("created-before"));
        if (params.containsKey("created-by")) createdBy = (String) params.get("created-by");
        if (params.containsKey("page-no")) pageNo = Integer.parseInt((String) params.get("page-no"));
        if (params.containsKey("page-size")) pageSize = Integer.parseInt((String) params.get("page-size"));
    }

    public PollParams(){

    }
}