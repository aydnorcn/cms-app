package com.aydnorcn.mis_app.dto.poll;

import com.aydnorcn.mis_app.dto.AuditResponse;
import com.aydnorcn.mis_app.dto.option.OptionResponse;
import com.aydnorcn.mis_app.entity.Poll;
import com.aydnorcn.mis_app.utils.PollType;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;

import java.util.List;

@Getter
public class PollResponse {

    private final String id;
    private final String title;
    private final String description;
    private final List<OptionResponse> options;
    private final PollType type;

    @JsonUnwrapped
    private final AuditResponse audits;

    public PollResponse(Poll poll) {
        this.id = poll.getId();
        this.title = poll.getTitle();
        this.description = poll.getDescription();
        this.options = poll.getOptions().stream().map(OptionResponse::new).toList();
        this.type = poll.getType();
        this.audits = new AuditResponse(poll.getCreatedAt(), poll.getUpdatedAt(), poll.getCreatedBy(), poll.getUpdatedBy());
    }
}
