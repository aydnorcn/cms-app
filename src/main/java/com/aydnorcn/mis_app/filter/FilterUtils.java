package com.aydnorcn.mis_app.filter;

import com.aydnorcn.mis_app.entity.User;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@UtilityClass
public class FilterUtils {

    public static <T> Specification<T> userEquals(String field, User user) {
        return (root, query, criteriaBuilder) -> user == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get(field), user);
    }

    public static <T> Specification<T> stringEquals(String field, String value) {
        return (root, query, criteriaBuilder) -> value == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get(field), value);
    }

    public static <T, U> Specification<T> equalsValue(String field, U value) {
        return (root, query, criteriaBuilder) -> value == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get(field), value);
    }

    public static <T,U> Specification<T> containsList(String field, U value) {
        return (root, query, criteriaBuilder) -> value == null ? criteriaBuilder.conjunction() : criteriaBuilder.isMember(value, root.get(field));
    }

    public static <T> Specification<T> listSizeGreaterThanOrEquals(String field, Integer size) {
        return (root, query, criteriaBuilder) -> size == null ? criteriaBuilder.conjunction() : criteriaBuilder.greaterThanOrEqualTo(criteriaBuilder.size(root.get(field)), size);
    }

    public static <T> Specification<T> listSizeLessThanOrEquals(String field, Integer size) {
        return (root, query, criteriaBuilder) -> size == null ? criteriaBuilder.conjunction() : criteriaBuilder.lessThanOrEqualTo(criteriaBuilder.size(root.get(field)), size);
    }

    public static <T> Specification<T> dateEquals(String field, LocalDate date) {
        return (root, query, criteriaBuilder) -> date == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get(field), date);
    }

    public static <T> Specification<T> afterDate(String field, LocalDateTime createdAfter) {
        return (root, query, criteriaBuilder) -> createdAfter == null ? criteriaBuilder.conjunction() : criteriaBuilder.greaterThanOrEqualTo(root.get(field), createdAfter);
    }

    public static <T> Specification<T> beforeDate(String field, LocalDateTime createdBefore) {
        return (root, query, criteriaBuilder) -> createdBefore == null ? criteriaBuilder.conjunction() : criteriaBuilder.lessThanOrEqualTo(root.get(field), createdBefore);
    }

    public static <T> Specification<T> afterTime(String field, LocalTime time){
        return (root, query, criteriaBuilder) -> time == null ? criteriaBuilder.conjunction() : criteriaBuilder.greaterThanOrEqualTo(root.get(field), time);
    }

    public static <T> Specification<T> beforeTime(String field, LocalTime time){
        return (root, query, criteriaBuilder) -> time == null ? criteriaBuilder.conjunction() : criteriaBuilder.lessThanOrEqualTo(root.get(field), time);
    }

    public static <T> Specification<T> greaterThanOrEqualsTo(String field, Integer number){
        return (root, query, criteriaBuilder) -> number == null ? criteriaBuilder.conjunction() : criteriaBuilder.greaterThanOrEqualTo(root.get(field), number);
    }

    public static <T> Specification<T> lessThanOrEqualsTo(String field, Integer number){
        return (root, query, criteriaBuilder) -> number == null ? criteriaBuilder.conjunction() : criteriaBuilder.lessThanOrEqualTo(root.get(field), number);
    }

    public static <T> Specification<T> isTrue(String field, Boolean value){
        return (root, query, criteriaBuilder) -> value == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get(field), value);
    }
}
