package com.aydnorcn.mis_app.utils.params;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
public class VoteParams extends PaginationParams {

    private String pollId = null;
    private String userId = null;
    private String optionId = null;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAfter = null;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdBefore = null;

    private Boolean isActive = null;

    public VoteParams(Map<String, Object> params){
        super(params);
        if(params.containsKey("poll-id")) pollId = (String) params.get("poll-id");
        if(params.containsKey("user-id")) userId = (String) params.get("user-id");
        if(params.containsKey("option-id")) optionId = (String) params.get("option-id");
        if(params.containsKey("created-after")) createdAfter = LocalDateTime.parse((String) params.get("created-after"));
        if(params.containsKey("created-before")) createdBefore = LocalDateTime.parse((String) params.get("created-before"));
        if(params.containsKey("is-active")) isActive = Boolean.parseBoolean((String) params.get("is-active"));
    }
}
