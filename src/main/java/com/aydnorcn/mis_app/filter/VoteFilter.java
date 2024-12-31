package com.aydnorcn.mis_app.filter;

import com.aydnorcn.mis_app.entity.Option;
import com.aydnorcn.mis_app.entity.Poll;
import com.aydnorcn.mis_app.entity.User;
import com.aydnorcn.mis_app.entity.Vote;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class VoteFilter {

    private VoteFilter(){
        throw new IllegalStateException("Utility class");
    }

    public static Specification<Vote> filter(User user, Option option, Poll poll,
                                             LocalDateTime votedAfter, LocalDateTime votedBefore,
                                             Boolean isActive){
        return Specification.where(userEquals(user))
                .and(optionEquals(option))
                .and(pollEquals(poll))
                .and(votedAfter(votedAfter))
                .and(votedBefore(votedBefore))
                .and(isActive(isActive));
    }

    public static Specification<Vote> userEquals(User user){
        return (root, query, criteriaBuilder) -> user == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("user"), user);
    }

    public static Specification<Vote> optionEquals(Option option){
        return (root, query, criteriaBuilder) -> option == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("option"), option);
    }

    public static Specification<Vote> pollEquals(Poll poll){
        return (root, query, criteriaBuilder) -> poll == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("option").get("poll"), poll);
    }

    public static Specification<Vote> votedAfter(LocalDateTime votedAfter){
        return (root, query, criteriaBuilder) -> votedAfter == null ? criteriaBuilder.conjunction() : criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), votedAfter);
    }

    public static Specification<Vote> votedBefore(LocalDateTime votedBefore){
        return (root, query, criteriaBuilder) -> votedBefore == null ? criteriaBuilder.conjunction() : criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), votedBefore);
    }

    public static Specification<Vote> isActive(Boolean isActive){
        return (root, query, criteriaBuilder) -> isActive == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("option").get("poll").get("isActive"), isActive);
    }
}
