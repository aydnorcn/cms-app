package com.aydnorcn.mis_app.filter;

import com.aydnorcn.mis_app.entity.Option;
import com.aydnorcn.mis_app.entity.Poll;
import com.aydnorcn.mis_app.entity.User;
import com.aydnorcn.mis_app.entity.Vote;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

import static com.aydnorcn.mis_app.filter.FilterUtils.*;

@UtilityClass
public class VoteFilter {

    public static Specification<Vote> filter(User user, Option option, Poll poll,
                                             LocalDateTime votedAfter, LocalDateTime votedBefore,
                                             Boolean isActive) {
        return Specification
                .where(FilterUtils.<Vote>userEquals("user", user))
                .and(equalsValue("option", option))
                .and(pollEquals(poll))
                .and(afterDate("createdAt", votedAfter))
                .and(beforeDate("createdAt", votedBefore))
                .and(isActive(isActive));
    }

    public static Specification<Vote> pollEquals(Poll poll) {
        return (root, query, criteriaBuilder) -> poll == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("option").get("poll"), poll);
    }

    public static Specification<Vote> isActive(Boolean isActive) {
        return (root, query, criteriaBuilder) -> isActive == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("option").get("poll").get("isActive"), isActive);
    }
}
