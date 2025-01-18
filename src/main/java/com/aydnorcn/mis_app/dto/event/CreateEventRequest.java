package com.aydnorcn.mis_app.dto.event;

import com.aydnorcn.mis_app.utils.EventStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class CreateEventRequest {

    @NotBlank(message = "Name cannot be null or empty!")
    private String name;

    @NotBlank(message = "Description cannot be null or empty!")
    private String description;

    @NotBlank(message = "Location cannot be null or empty!")
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

    public CreateEventRequest(String name, String description, String location, LocalDate date, LocalTime startTime, LocalTime endTime, EventStatus status) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public CreateEventRequest(){

    }
}
