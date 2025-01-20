package com.aydnorcn.mis_app.service;

import com.aydnorcn.mis_app.dto.PageResponseDto;
import com.aydnorcn.mis_app.dto.vote.VoteRequest;
import com.aydnorcn.mis_app.entity.Option;
import com.aydnorcn.mis_app.entity.Vote;
import com.aydnorcn.mis_app.exception.APIException;
import com.aydnorcn.mis_app.exception.ResourceNotFoundException;
import com.aydnorcn.mis_app.filter.VoteFilter;
import com.aydnorcn.mis_app.repository.VoteRepository;
import com.aydnorcn.mis_app.utils.MessageConstants;
import com.aydnorcn.mis_app.utils.PollType;
import com.aydnorcn.mis_app.utils.params.VoteParams;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.VOTE_NOT_FOUND));
    }

    public PageResponseDto<Vote> getVotes(VoteParams params) {
        var user = (params.getUserId() == null) ? null : userService.getUserById(params.getUserId());
        var option = (params.getOptionId() == null) ? null : optionService.getOptionById(params.getOptionId());
        var poll = (params.getPollId() == null) ? null : pollService.getPollById(params.getPollId());

        Specification<Vote> specification = VoteFilter.filter(user, option, poll,
                params.getCreatedAfter(), params.getCreatedBefore(), params.getIsActive());

        Sort sort = params.getSortOrder().equalsIgnoreCase("asc")
                ? Sort.by(params.getSortBy()).ascending()
                : Sort.by(params.getSortBy()).descending();

        Page<Vote> page = voteRepository.findAll(specification, PageRequest.of(params.getPageNo(), params.getPageSize(), sort));

        return new PageResponseDto<>(page);
    }

    @Transactional
    public Vote createVote(VoteRequest request) {
        Option option = optionService.getOptionById(request.getOptionId());

        if (!option.getPoll().isActive()) {
            throw new APIException(HttpStatus.BAD_REQUEST, MessageConstants.POLL_IS_NOT_ACTIVE);
        }

        if (option.getPoll().getType().equals(PollType.SINGLE_CHOICE)) {
            return createSingleChoiceVote(option);
        } else {
            return createMultipleChoiceVote(option);
        }
    }

    public void deleteVote(String voteId) {
        Vote vote = getVoteById(voteId);
        voteRepository.delete(vote);
    }

    private Vote createSingleChoiceVote(Option option) {
        Optional<Vote> optionalVote = voteRepository.findByOptionPoll(option.getPoll());

        optionalVote.ifPresent(voteRepository::delete);

        Vote vote = new Vote();
        vote.setUser(userContextService.getCurrentAuthenticatedUser());
        vote.setOption(option);

        return voteRepository.save(vote);
    }

    private Vote createMultipleChoiceVote(Option option) {
        int count = voteRepository.countByOptionPollAndUser(option.getPoll(), userContextService.getCurrentAuthenticatedUser());

        if (count >= option.getPoll().getMaxVoteCount()) {
            throw new APIException(HttpStatus.BAD_REQUEST, MessageConstants.MAX_VOTE_COUNT_EXCEEDED);
        }

        Vote vote = new Vote();
        vote.setUser(userContextService.getCurrentAuthenticatedUser());
        vote.setOption(option);

        return voteRepository.save(vote);
    }


    public boolean isAuthenticatedUserOwnerOfVote(String voteId) {
        Vote vote = getVoteById(voteId);
        return vote.getUser().getId().equals(userContextService.getCurrentAuthenticatedUser().getId());
    }
}