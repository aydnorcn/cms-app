package com.aydnorcn.mis_app.utils.params;

import com.aydnorcn.mis_app.utils.params.commons.CreatedDateRangeParams;
import com.aydnorcn.mis_app.utils.params.commons.PaginationParams;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;

import java.util.Map;

@Getter
public class AssignmentParams extends PaginationParams {

    private String assignedTo = null;
    private String eventId = null;
    private Boolean isCompleted = null;
    private Integer minPriority = null;
    private Integer maxPriority = null;

    @JsonUnwrapped
    private CreatedDateRangeParams createdDateRangeParams;

    private String createdBy = null;

    public AssignmentParams(Map<String, Object> params) {
        super(params);
        createdDateRangeParams = new CreatedDateRangeParams(params);
        if (params.containsKey("assigned-to")) assignedTo = (String) params.get("assigned-to");
        if (params.containsKey("event-id")) eventId = (String) params.get("event-id");
        if (params.containsKey("is-completed")) isCompleted = Boolean.parseBoolean((String) params.get("is-completed"));
        if (params.containsKey("min-priority")) minPriority = Integer.parseInt((String) params.get("min-priority"));
        if (params.containsKey("max-priority")) maxPriority = Integer.parseInt((String) params.get("max-priority"));
        if (params.containsKey("created-by")) createdBy = (String) params.get("created-by");
    }
}
