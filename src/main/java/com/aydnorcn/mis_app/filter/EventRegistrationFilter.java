package com.aydnorcn.mis_app.filter;

import com.aydnorcn.mis_app.entity.Event;
import com.aydnorcn.mis_app.entity.EventRegistration;
import com.aydnorcn.mis_app.entity.User;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class EventRegistrationFilter {

    public static Specification<EventRegistration> filter(User user, Event event) {
        return Specification.where(userEquals(user))
                .and(eventEquals(event));
    }

    public static Specification<EventRegistration> userEquals(User user) {
        return (root, query, criteriaBuilder) -> user == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("user"), user);
    }

    public static Specification<EventRegistration> eventEquals(Event event) {
        return (root, query, criteriaBuilder) -> event == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("event"), event);
    }
}
