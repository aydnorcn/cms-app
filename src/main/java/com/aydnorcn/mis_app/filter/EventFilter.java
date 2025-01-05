package com.aydnorcn.mis_app.filter;

import com.aydnorcn.mis_app.entity.Event;
import com.aydnorcn.mis_app.entity.User;
import com.aydnorcn.mis_app.utils.EventStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalTime;

public class EventFilter {
    private EventFilter(){
        throw new IllegalStateException("Utility class");
    }

    public static Specification<Event> filter(String location, LocalDate date, LocalTime startAfter, LocalTime endBefore,
                                              EventStatus status, User createdBy) {
        return Specification.where(locationEquals(location))
                .and(dateEquals(date))
                .and(startAfter(startAfter))
                .and(endBefore(endBefore))
                .and(statusEquals(status))
                .and(createdBy(createdBy));
    }

    public static Specification<Event> locationEquals(String location) {
        return (root, query, criteriaBuilder) -> location == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("location"), location);
    }

    public static Specification<Event> dateEquals(LocalDate date) {
        return (root, query, criteriaBuilder) -> date == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("date"), date);
    }

    public static Specification<Event> startAfter(LocalTime startAfter) {
        return (root, query, criteriaBuilder) -> startAfter == null ? criteriaBuilder.conjunction() : criteriaBuilder.greaterThanOrEqualTo(root.get("startTime"), startAfter);
    }

    public static Specification<Event> endBefore(LocalTime endBefore) {
        return (root, query, criteriaBuilder) -> endBefore == null ? criteriaBuilder.conjunction() : criteriaBuilder.lessThanOrEqualTo(root.get("endTime"), endBefore);
    }

    public static Specification<Event> statusEquals(EventStatus status) {
        return (root, query, criteriaBuilder) -> status == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Event> createdBy(User createdBy) {
        return (root, query, criteriaBuilder) -> createdBy == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("createdBy"), createdBy);
    }
}
