package com.aydnorcn.mis_app.filter;

import com.aydnorcn.mis_app.entity.Assignment;
import com.aydnorcn.mis_app.entity.Event;
import com.aydnorcn.mis_app.entity.User;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

import static com.aydnorcn.mis_app.filter.FilterUtils.*;

@UtilityClass
public class AssignmentFilter {

    public static Specification<Assignment> filter(User assignedTo, Event event, Boolean isCompleted,
                                                   Integer minPriority, Integer maxPriority,
                                                   LocalDateTime createdAfter, LocalDateTime createdBefore,
                                                   User createdBy) {
        return Specification
                .where(FilterUtils.<Assignment, User>containsList("assignedTo", assignedTo))
                .and(equalsValue("event", event))
                .and(isTrue("isCompleted", isCompleted))
                .and(greaterThanOrEqualsTo("priority", minPriority))
                .and(lessThanOrEqualsTo("priority", maxPriority))
                .and(afterDate("createdAt", createdAfter))
                .and(beforeDate("createdAt", createdBefore))
                .and(stringEquals("createdBy", (createdBy != null) ? createdBy.getId() : null));
    }
}