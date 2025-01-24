package com.aydnorcn.mis_app.strategy;

import com.aydnorcn.mis_app.entity.Option;
import com.aydnorcn.mis_app.entity.Vote;
import com.aydnorcn.mis_app.repository.VoteRepository;
import com.aydnorcn.mis_app.service.UserContextService;
import com.aydnorcn.mis_app.utils.PollType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SingleChoiceVoteStrategy implements CreateVoteStrategy {

    private final VoteRepository voteRepository;
    private final UserContextService userContextService;

    @Override
    public Vote createVote(Option option) {
        Optional<Vote> optionalVote = voteRepository.findByOptionPoll(option.getPoll());

        optionalVote.ifPresent(voteRepository::delete);

        Vote vote = new Vote();
        vote.setUser(userContextService.getCurrentAuthenticatedUser());
        vote.setOption(option);

        return voteRepository.save(vote);
    }

    @Override
    public PollType getPollType() {
        return PollType.SINGLE_CHOICE;
    }
}
