package com.aydnorcn.mis_app.dto.event;

import com.aydnorcn.mis_app.utils.EventStatus;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;


@Getter
public class PatchEventRequest {

    private String name;

    private String description;

    private String location;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    private EventStatus status;
}
