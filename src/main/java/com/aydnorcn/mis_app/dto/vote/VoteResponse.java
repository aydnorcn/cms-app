package com.aydnorcn.mis_app.dto.vote;

import com.aydnorcn.mis_app.dto.AuditResponse;
import com.aydnorcn.mis_app.entity.Vote;
import lombok.Getter;

@Getter
public class VoteResponse{

    private final String id;
    private final String userId;
    private final String optionId;
    private final String pollId;
    private final AuditResponse audits;


    public VoteResponse(Vote vote) {
        this.id = vote.getId();
        this.userId = vote.getUser().getId();
        this.optionId = vote.getOption().getId();
        this.pollId = vote.getOption().getPoll().getId();
        this.audits = new AuditResponse(vote.getCreatedAt(), vote.getUpdatedAt(), null, null);
    }
}
