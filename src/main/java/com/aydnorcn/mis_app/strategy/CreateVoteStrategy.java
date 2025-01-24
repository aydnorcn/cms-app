package com.aydnorcn.mis_app.strategy;

import com.aydnorcn.mis_app.entity.Option;
import com.aydnorcn.mis_app.entity.Vote;
import com.aydnorcn.mis_app.utils.PollType;

public interface CreateVoteStrategy {

    Vote createVote(Option option);

    PollType getPollType();
}
