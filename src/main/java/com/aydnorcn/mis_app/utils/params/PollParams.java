package com.aydnorcn.mis_app.utils.params;

import com.aydnorcn.mis_app.utils.PollType;
import com.aydnorcn.mis_app.utils.params.commons.CreatedDateRangeParams;
import com.aydnorcn.mis_app.utils.params.commons.PaginationParams;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class PollParams extends PaginationParams {

    private PollType type = null;
    private Integer minOptionCount = null;
    private Integer maxOptionCount = null;

    @JsonUnwrapped
    private CreatedDateRangeParams createdDateRangeParams;

    private String createdBy = null;

    public PollParams(Map<String, Object> params) {
        super(params);
        createdDateRangeParams = new CreatedDateRangeParams(params);
        if (params.containsKey("poll-type")) type = PollType.fromString((String) params.get("poll-type"));
        if (params.containsKey("min-option-count")) minOptionCount = Integer.parseInt((String) params.get("min-option-count"));
        if (params.containsKey("max-option-count")) maxOptionCount = Integer.parseInt((String) params.get("max-option-count"));
        if (params.containsKey("created-by")) createdBy = (String) params.get("created-by");
    }
}