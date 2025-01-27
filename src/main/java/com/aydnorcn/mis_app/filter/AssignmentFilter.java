package com.aydnorcn.mis_app.filter;

import com.aydnorcn.mis_app.entity.Assignment;
import com.aydnorcn.mis_app.entity.Event;
import com.aydnorcn.mis_app.entity.User;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class AssignmentFilter {

    private AssignmentFilter() {
        throw new IllegalStateException("Utility class");
    }

    public static Specification<Assignment> filter(User assignedTo, Event event, Boolean isCompleted,
                                                   Integer minPriority, Integer maxPriority,
                                                   LocalDateTime createdAfter, LocalDateTime createdBefore,
                                                   User createdBy) {
        return Specification.where(assignedToEquals(assignedTo))
                .and(eventEquals(event))
                .and(isCompletedEquals(isCompleted))
                .and(isPriorityGreaterThanOrEqualTo(minPriority))
                .and(isPriorityLessThanOrEqualTo(maxPriority))
                .and(createdAfter(createdAfter))
                .and(createdBefore(createdBefore))
                .and(createdBy(createdBy));
    }

    public static Specification<Assignment> assignedToEquals(User assignedTo) {
        return (root, query, criteriaBuilder) -> assignedTo == null ? criteriaBuilder.conjunction() : criteriaBuilder.isMember(assignedTo, root.get("assignedTo"));
    }

    public static Specification<Assignment> eventEquals(Event event) {
        return (root, query, criteriaBuilder) -> event == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("event"), event);
    }

    public static Specification<Assignment> isCompletedEquals(Boolean isCompleted) {
        return (root, query, criteriaBuilder) -> isCompleted == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("isCompleted"), isCompleted);
    }

    public static Specification<Assignment> isPriorityGreaterThanOrEqualTo(Integer minPriority) {
        return (root, query, criteriaBuilder) -> minPriority == null ? criteriaBuilder.conjunction() : criteriaBuilder.greaterThanOrEqualTo(root.get("priority"), minPriority);
    }

    public static Specification<Assignment> isPriorityLessThanOrEqualTo(Integer maxPriority) {
        return (root, query, criteriaBuilder) -> maxPriority == null ? criteriaBuilder.conjunction() : criteriaBuilder.lessThanOrEqualTo(root.get("priority"), maxPriority);
    }

    public static Specification<Assignment> createdAfter(LocalDateTime createdAfter) {
        return (root, query, criteriaBuilder) -> createdAfter == null ? criteriaBuilder.conjunction() : criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), createdAfter);
    }

    public static Specification<Assignment> createdBefore(LocalDateTime createdBefore) {
        return (root, query, criteriaBuilder) -> createdBefore == null ? criteriaBuilder.conjunction() : criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), createdBefore);
    }

    public static Specification<Assignment> createdBy(User createdBy) {
        return (root, query, criteriaBuilder) -> createdBy == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("createdBy"), createdBy);
    }
}