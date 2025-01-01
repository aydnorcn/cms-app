package com.aydnorcn.mis_app.filter;

import com.aydnorcn.mis_app.entity.Poll;
import com.aydnorcn.mis_app.entity.User;
import com.aydnorcn.mis_app.utils.PollType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class PollFilter {

    private PollFilter(){
        throw new IllegalStateException("Utility class");
    }

    public static Specification<Poll> filter(PollType type, Integer minOptionCount, Integer maxOptionCount,
                                             LocalDateTime createdAfter, LocalDateTime createdBefore,
                                             User createdBy) {
        return Specification.where(typeEquals(type))
                .and(optionCountGreaterThanOrEquals(minOptionCount))
                .and(optionCountLessThanOrEquals(maxOptionCount))
                .and(createdAfter(createdAfter))
                .and(createdBefore(createdBefore))
                .and(createdBy(createdBy));
    }


    public static Specification<Poll> typeEquals(PollType type) {
        return (root, query, criteriaBuilder) -> type == null ? criteriaBuilder.conjunction() :  criteriaBuilder.equal(root.get("type"), type);
    }

    public static Specification<Poll> optionCountGreaterThanOrEquals(Integer count) {
        return (root, query, criteriaBuilder) -> count == null ? criteriaBuilder.conjunction() : criteriaBuilder.greaterThanOrEqualTo(criteriaBuilder.size(root.get("options")), count);
    }

    public static Specification<Poll> optionCountLessThanOrEquals(Integer count) {
        return (root, query, criteriaBuilder) -> count == null ? criteriaBuilder.conjunction() : criteriaBuilder.lessThanOrEqualTo(criteriaBuilder.size(root.get("options")), count);
    }

    public static Specification<Poll> createdAfter(LocalDateTime createdAfter) {
        return (root, query, criteriaBuilder) -> createdAfter == null ? criteriaBuilder.conjunction() : criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), createdAfter);
    }

    public static Specification<Poll> createdBefore(LocalDateTime createdBefore) {
        return (root, query, criteriaBuilder) -> createdBefore == null ? criteriaBuilder.conjunction() : criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), createdBefore);
    }

    public static Specification<Poll> createdBy(User createdBy) {
        return (root, query, criteriaBuilder) -> createdBy == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("createdBy"), createdBy);
    }
}
