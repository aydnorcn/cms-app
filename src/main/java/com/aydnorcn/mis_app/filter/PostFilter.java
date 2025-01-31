package com.aydnorcn.mis_app.filter;

import com.aydnorcn.mis_app.entity.Category;
import com.aydnorcn.mis_app.entity.Post;
import com.aydnorcn.mis_app.entity.User;
import com.aydnorcn.mis_app.utils.PostStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

public class PostFilter {

    private PostFilter() {
        throw new IllegalStateException("Utility class");
    }

    public static Specification<Post> filter(User author, Category category, List<PostStatus> status, LocalDateTime createdAtAfter, LocalDateTime createdAtBefore) {
        return Specification.where(authorEquals(author))
                .and(categoryEquals(category))
                .and(statusEquals(status))
                .and(createdAtAfter(createdAtAfter))
                .and(createdAtBefore(createdAtBefore));
    }

    public static Specification<Post> authorEquals(User author) {
        return (root, query, criteriaBuilder) -> author == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("author"), author);
    }

    public static Specification<Post> categoryEquals(Category category) {
        return (root, query, criteriaBuilder) -> category == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("category"), category);
    }

    public static Specification<Post> statusEquals(List<PostStatus> status) {
        return (root, query, criteriaBuilder) -> status.isEmpty() ? criteriaBuilder.conjunction() : root.get("status").in(status);
    }

    public static Specification<Post> createdAtAfter(LocalDateTime createdAtAfter) {
        return (root, query, criteriaBuilder) -> createdAtAfter == null ? criteriaBuilder.conjunction() : criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), createdAtAfter);
    }

    public static Specification<Post> createdAtBefore(LocalDateTime createdAtBefore) {
        return (root, query, criteriaBuilder) -> createdAtBefore == null ? criteriaBuilder.conjunction() : criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), createdAtBefore);
    }
}
