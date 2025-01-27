package com.aydnorcn.mis_app.utils.params;

import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
public class AssignmentParams extends PaginationParams {

    private String assignedTo = null;
    private String eventId = null;
    private Boolean isCompleted = null;
    private Integer minPriority = null;
    private Integer maxPriority = null;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAfter = null;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdBefore = null;

    private String createdBy = null;

    public AssignmentParams(Map<String, Object> params) {
        super(params);
        if (params.containsKey("assigned-to")) assignedTo = (String) params.get("assigned-to");
        if (params.containsKey("event-id")) eventId = (String) params.get("event-id");
        if (params.containsKey("is-completed")) isCompleted = Boolean.parseBoolean((String) params.get("is-completed"));
        if (params.containsKey("min-priority")) minPriority = Integer.parseInt((String) params.get("min-priority"));
        if (params.containsKey("max-priority")) maxPriority = Integer.parseInt((String) params.get("max-priority"));
        if (params.containsKey("created-after"))
            createdAfter = LocalDateTime.parse((String) params.get("created-after"));
        if (params.containsKey("created-before"))
            createdBefore = LocalDateTime.parse((String) params.get("created-before"));
        if (params.containsKey("created-by")) createdBy = (String) params.get("created-by");
    }
}
