package com.aydnorcn.mis_app.service;

import com.aydnorcn.mis_app.dto.PageResponseDto;
import com.aydnorcn.mis_app.dto.vote.VoteRequest;
import com.aydnorcn.mis_app.entity.Option;
import com.aydnorcn.mis_app.entity.Vote;
import com.aydnorcn.mis_app.exception.ResourceNotFoundException;
import com.aydnorcn.mis_app.filter.VoteFilter;
import com.aydnorcn.mis_app.repository.VoteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final UserService userService;
    private final UserContextService userContextService;
    private final OptionService optionService;
    private final PollService pollService;

    public Vote getVoteById(String voteId) {
        return voteRepository.findById(voteId)
                .orElseThrow(() -> new ResourceNotFoundException("Vote not found with id: " + voteId));
    }

    public PageResponseDto<Vote> getVotes(String userId, String optionId, String pollId,
                                          LocalDateTime votedAfter, LocalDateTime votedBefore,
                                          Boolean isActive,
                                          int pageNo, int pageSize) {
        var user = (userId == null) ? null : userService.getUserById(userId);
        var option = (optionId == null) ? null : optionService.getOptionById(optionId);
        var poll = (pollId == null) ? null : pollService.getPollById(pollId);

        Specification<Vote> specification = VoteFilter.filter(user, option, poll, votedAfter, votedBefore, isActive);
        Page<Vote> page = voteRepository.findAll(specification, PageRequest.of(pageNo, pageSize));

        return new PageResponseDto<>(page);
    }

    @Transactional
    public Vote createVote(VoteRequest request) {
        Option option = optionService.getOptionById(request.getOptionId());

        if (!option.getPoll().isActive()) {
            throw new ResourceNotFoundException("Poll is not active");
            //TODO: New Exception Type
        }

        Optional<Vote> optionalVote = voteRepository.findByOptionPoll(option.getPoll());

        optionalVote.ifPresent(voteRepository::delete);

        Vote vote = new Vote();
        vote.setUser(userContextService.getCurrentAuthenticatedUser());
        vote.setOption(option);

        return voteRepository.save(vote);
    }

    public void deleteVote(String voteId) {
        Vote vote = getVoteById(voteId);
        voteRepository.delete(vote);
    }


    public boolean isAuthenticatedUserOwnerOfVote(String voteId) {
        Vote vote = getVoteById(voteId);
        return vote.getUser().getId().equals(userContextService.getCurrentAuthenticatedUser().getId());
    }
}