package com.aydnorcn.mis_app.utils.params;

import com.aydnorcn.mis_app.utils.params.commons.CreatedDateRangeParams;
import com.aydnorcn.mis_app.utils.params.commons.PaginationParams;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class VoteParams extends PaginationParams {

    private String pollId = null;
    private String userId = null;
    private String optionId = null;

    @JsonUnwrapped
    private CreatedDateRangeParams createdDateRangeParams;

    private Boolean isActive = null;

    public VoteParams(Map<String, Object> params) {
        super(params);
        createdDateRangeParams = new CreatedDateRangeParams(params);
        if (params.containsKey("poll-id")) pollId = (String) params.get("poll-id");
        if (params.containsKey("user-id")) userId = (String) params.get("user-id");
        if (params.containsKey("option-id")) optionId = (String) params.get("option-id");
        if (params.containsKey("is-active")) isActive = Boolean.parseBoolean((String) params.get("is-active"));
    }
}
