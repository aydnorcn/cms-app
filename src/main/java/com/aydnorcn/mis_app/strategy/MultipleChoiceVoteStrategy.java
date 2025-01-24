package com.aydnorcn.mis_app.strategy;

import com.aydnorcn.mis_app.entity.Option;
import com.aydnorcn.mis_app.entity.Vote;
import com.aydnorcn.mis_app.exception.APIException;
import com.aydnorcn.mis_app.repository.VoteRepository;
import com.aydnorcn.mis_app.service.UserContextService;
import com.aydnorcn.mis_app.utils.MessageConstants;
import com.aydnorcn.mis_app.utils.PollType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MultipleChoiceVoteStrategy implements CreateVoteStrategy {

    private final VoteRepository voteRepository;
    private final UserContextService userContextService;

    @Override
    public Vote createVote(Option option) {
        int count = voteRepository.countByOptionPollAndUser(option.getPoll(), userContextService.getCurrentAuthenticatedUser());

        if (count >= option.getPoll().getMaxVoteCount()) {
            throw new APIException(HttpStatus.BAD_REQUEST, MessageConstants.MAX_VOTE_COUNT_EXCEEDED);
        }

        Vote vote = new Vote();
        vote.setUser(userContextService.getCurrentAuthenticatedUser());
        vote.setOption(option);

        return voteRepository.save(vote);
    }

    @Override
    public PollType getPollType() {
        return PollType.MULTIPLE_CHOICE;
    }
}
