package com.aydnorcn.mis_app.utils.params;

import com.aydnorcn.mis_app.utils.EventStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@Getter
@Setter
public class EventParams {

    private String location = null;
    private LocalDate date = null;
    private LocalTime startAfter = null;
    private LocalTime endBefore = null;
    private EventStatus status = null;
    private String createdBy = null;
    private int pageNo = 0;
    private int pageSize = 10;


    public EventParams(Map<String, Object> params){
        if(params.containsKey("location")) location = (String) params.get("location");
        if(params.containsKey("date")) date = LocalDate.parse((String) params.get("date"));
        if(params.containsKey("start-after")) startAfter = LocalTime.parse((String) params.get("start-after"));
        if(params.containsKey("end-before")) endBefore = LocalTime.parse((String) params.get("end-before"));
        if(params.containsKey("status")) status = EventStatus.fromString((String) params.get("status"));
        if(params.containsKey("created-by")) createdBy = (String) params.get("created-by");
        if(params.containsKey("page-no")) pageNo = Integer.parseInt((String) params.get("page-no"));
        if(params.containsKey("page-size")) pageSize = Integer.parseInt((String) params.get("page-size"));
    }
}