package com.aydnorcn.mis_app.dto.poll;

import com.aydnorcn.mis_app.entity.Option;
import com.aydnorcn.mis_app.entity.Poll;
import com.aydnorcn.mis_app.utils.PollType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PollResponse {

    private final String id;
    private final String title;
    private final String description;
    private final List<OptionResponse> options;
    private final PollType type;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final String createdBy;
    private final String updatedBy;

    public PollResponse(Poll poll) {
        this.id = poll.getId();
        this.title = poll.getTitle();
        this.description = poll.getDescription();
        this.options = poll.getOptions().stream().map(OptionResponse::new).toList();
        this.type = poll.getType();
        this.createdAt = poll.getCreatedAt();
        this.updatedAt = poll.getUpdatedAt();
        this.createdBy = poll.getCreatedBy();
        this.updatedBy = poll.getUpdatedBy();
    }

    @Getter
    private static class OptionResponse {
        private final String id;
        private final String text;
        private final int voteCount;

        public OptionResponse(Option option) {
            this.id = option.getId();
            this.text = option.getText();
            this.voteCount = option.getVotes().size();
        }
    }
}
