package com.aydnorcn.mis_app.dto.event_registration;

import com.aydnorcn.mis_app.dto.AuditResponse;
import com.aydnorcn.mis_app.dto.event.EventResponse;
import com.aydnorcn.mis_app.entity.EventRegistration;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventRegistrationResponse {

    private final String id;
    private final EventResponse eventResponse;

    @JsonUnwrapped
    private final AuditResponse audit;

    public EventRegistrationResponse(EventRegistration eventRegistration) {
        this.id = eventRegistration.getId();
        this.eventResponse = new EventResponse(eventRegistration.getEvent());
        this.audit = new AuditResponse(eventRegistration.getCreatedAt(), null, eventRegistration.getUser().getId(), null);
    }
}
