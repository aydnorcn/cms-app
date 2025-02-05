package com.aydnorcn.mis_app.filter;

import com.aydnorcn.mis_app.entity.Poll;
import com.aydnorcn.mis_app.entity.User;
import com.aydnorcn.mis_app.utils.PollType;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

import static com.aydnorcn.mis_app.filter.FilterUtils.*;

@UtilityClass
public class PollFilter {

    public static Specification<Poll> filter(PollType type, Integer minOptionCount, Integer maxOptionCount,
                                             LocalDateTime createdAfter, LocalDateTime createdBefore,
                                             User createdBy) {
        return Specification
                .where(FilterUtils.<Poll, PollType>equalsValue("type", type))
                .and(listSizeGreaterThanOrEquals("options", minOptionCount))
                .and(listSizeLessThanOrEquals("options", maxOptionCount))
                .and(afterDate("createdAt", createdAfter))
                .and(beforeDate("createdAt", createdBefore))
                .and(stringEquals("createdBy", (createdBy != null) ? createdBy.getId() : null));
    }
}
