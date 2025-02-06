package com.aydnorcn.mis_app.filter;

import com.aydnorcn.mis_app.entity.Event;
import com.aydnorcn.mis_app.entity.User;
import com.aydnorcn.mis_app.utils.EventStatus;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalTime;

import static com.aydnorcn.mis_app.filter.FilterUtils.*;

@UtilityClass
public class EventFilter {

    public static Specification<Event> filter(String location, LocalDate date, LocalTime startAfter, LocalTime endBefore,
                                              EventStatus status, User createdBy) {
        return Specification
                .where(FilterUtils.<Event>stringEquals("location", location))
                .and(dateEquals("date", date))
                .and(afterTime("startTime", startAfter))
                .and(beforeTime("endTime", endBefore))
                .and(equalsValue("status", status))
                .and(stringEquals("createdBy", (createdBy != null) ? createdBy.getId() : null));
    }
}
