package com.aydnorcn.mis_app.filter;

import com.aydnorcn.mis_app.entity.Category;
import com.aydnorcn.mis_app.entity.Post;
import com.aydnorcn.mis_app.entity.User;
import com.aydnorcn.mis_app.utils.PostStatus;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

import static com.aydnorcn.mis_app.filter.FilterUtils.*;

@UtilityClass
public class PostFilter {

    public static Specification<Post> filter(User author, Category category, List<PostStatus> status, LocalDateTime createdAtAfter, LocalDateTime createdAtBefore) {
        return Specification
                .where(FilterUtils.<Post>userEquals("author", author))
                .and(equalsValue("category", category))
                .and(statusEquals(status))
                .and(afterDate("createdAt", createdAtAfter))
                .and(beforeDate("createdAt", createdAtBefore));
    }

    public static Specification<Post> statusEquals(List<PostStatus> status) {
        return (root, query, criteriaBuilder) -> status.isEmpty() ? criteriaBuilder.conjunction() : root.get("status").in(status);
    }
}
