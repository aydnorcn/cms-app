package com.aydnorcn.mis_app.dto.event;

import com.aydnorcn.mis_app.dto.AuditResponse;
import com.aydnorcn.mis_app.entity.Event;
import com.aydnorcn.mis_app.utils.EventStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class EventResponse {

    private final String id;
    private final String name;
    private final String description;
    private final String location;

    @JsonFormat(pattern = "dd.MM.yyyy")
    private final LocalDate date;

    @JsonFormat(pattern = "HH:mm")
    private final LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    private final LocalTime endTime;
    private final EventStatus status;

    private final AuditResponse audits;

    public EventResponse(Event event) {
        this.id = event.getId();
        this.name = event.getName();
        this.description = event.getDescription();
        this.location = event.getLocation();
        this.date = event.getDate();
        this.startTime = event.getStartTime();
        this.endTime = event.getEndTime();
        this.status = event.getStatus();
        this.audits = new AuditResponse(event.getCreatedAt(), event.getUpdatedAt(), event.getCreatedBy(), event.getUpdatedBy());
    }
}
