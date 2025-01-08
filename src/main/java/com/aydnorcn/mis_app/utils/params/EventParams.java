package com.aydnorcn.mis_app.utils.params;

import com.aydnorcn.mis_app.utils.EventStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@Getter
@Setter
public class EventParams extends PaginationParams{

    private String location = null;
    private LocalDate date = null;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime startAfter = null;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime endBefore = null;

    private EventStatus status = null;
    private String createdBy = null;


    public EventParams(Map<String, Object> params) {
        super(params);
        if (params.containsKey("location")) location = (String) params.get("location");
        if (params.containsKey("date")) date = LocalDate.parse((String) params.get("date"));
        if (params.containsKey("start-after")) startAfter = LocalTime.parse((String) params.get("start-after"));
        if (params.containsKey("end-before")) endBefore = LocalTime.parse((String) params.get("end-before"));
        if (params.containsKey("status")) status = EventStatus.fromString((String) params.get("status"));
        if (params.containsKey("created-by")) createdBy = (String) params.get("created-by");
    }

    public EventParams() {

    }
}