package com.aydnorcn.mis_app.utils.params;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class EventRegistrationParams extends PaginationParams {
    private String eventId = null;
    private String userId = null;

    public EventRegistrationParams(Map<String, Object> params) {
        super(params);
        if (params.containsKey("event-id")) eventId = (String) params.get("event-id");
        if (params.containsKey("user-id")) userId = (String) params.get("user-id");
    }
}