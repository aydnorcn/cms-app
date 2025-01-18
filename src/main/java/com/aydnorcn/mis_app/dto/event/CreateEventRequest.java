package com.aydnorcn.mis_app.dto.event;

import com.aydnorcn.mis_app.utils.EventStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

import static com.aydnorcn.mis_app.utils.MessageConstants.*;

@Getter
@Setter
public class CreateEventRequest {

    @NotBlank(message = NAME_NOT_BLANK)
    private String name;

    @NotBlank(message = DESCRIPTION_NOT_BLANK)
    private String description;

    @NotBlank(message = LOCATION_NOT_BLANK)
    private String location;

    @NotNull(message = DATE_NOT_NULL)
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate date;

    @NotNull(message = START_TIME_NOT_NULL)
    private LocalTime startTime;

    @NotNull(message = END_TIME_NOT_NULL)
    private LocalTime endTime;

    @NotNull(message = STATUS_NOT_NULL)
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
