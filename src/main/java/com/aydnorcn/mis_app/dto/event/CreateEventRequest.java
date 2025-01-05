package com.aydnorcn.mis_app.dto.event;

import com.aydnorcn.mis_app.utils.EventStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class CreateEventRequest {

    @NotNull(message = "Name cannot be null or empty!")
    private String name;

    @NotNull(message = "Description cannot be null or empty!")
    private String description;

    @NotNull(message = "Location cannot be null or empty!")
    private String location;

    @NotNull(message = "Date cannot be null or empty!")
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate date;

    @NotNull(message = "Start time cannot be null or empty!")
    private LocalTime startTime;

    @NotNull(message = "End time cannot be null or empty!")
    private LocalTime endTime;

    @NotNull(message = "Status cannot be null or empty!")
    private EventStatus status;
}
