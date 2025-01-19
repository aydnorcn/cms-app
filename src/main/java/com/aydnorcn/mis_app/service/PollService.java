package com.aydnorcn.mis_app.service;

import com.aydnorcn.mis_app.dto.PageResponseDto;
import com.aydnorcn.mis_app.dto.poll.CreatePollRequest;
import com.aydnorcn.mis_app.dto.poll.PatchPollRequest;
import com.aydnorcn.mis_app.entity.Option;
import com.aydnorcn.mis_app.entity.Poll;
import com.aydnorcn.mis_app.exception.ResourceNotFoundException;
import com.aydnorcn.mis_app.filter.PollFilter;
import com.aydnorcn.mis_app.repository.PollRepository;
import com.aydnorcn.mis_app.utils.MessageConstants;
import com.aydnorcn.mis_app.utils.params.PollParams;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PollService {

    private final PollRepository pollRepository;
    private final UserService userService;

    public Poll getPollById(String pollId) {
        return pollRepository.findById(pollId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.POLL_NOT_FOUND));
    }

    public PageResponseDto<Poll> getPolls(PollParams params) {

        Specification<Poll> specification = PollFilter.filter(params.getType(), params.getMinOptionCount(), params.getMaxOptionCount(),
                params.getCreatedAfter(), params.getCreatedBefore(), (params.getCreatedBy() == null ? null : userService.getUserById(params.getCreatedBy())));

        Page<Poll> page = pollRepository.findAll(specification, PageRequest.of(params.getPageNo(), params.getPageSize()));

        return new PageResponseDto<>(page);
    }

    public Poll createPoll(CreatePollRequest request) {
        Poll poll = new Poll();

        updatePollFields(poll, request);

        return pollRepository.saveAndFlush(poll);
    }

    public Poll updatePoll(String pollId, CreatePollRequest request) {
        Poll poll = getPollById(pollId);

        updatePollFields(poll, request);

        return pollRepository.saveAndFlush(poll);
    }

    public Poll patchPoll(String pollId, PatchPollRequest request) {
        Poll poll = getPollById(pollId);

        patchPollFields(poll, request);

        return pollRepository.save(poll);
    }

    public void deletePoll(String pollId) {
        Poll poll = getPollById(pollId);
        pollRepository.delete(poll);
    }

    private void updatePollFields(Poll poll, CreatePollRequest request) {
        poll.setTitle(request.getTitle());
        poll.setDescription(request.getDescription());

        poll.setType(request.getType());

        List<Option> options = request.getChoices().stream()
                .map(option -> {
                    Option newOption = new Option();
                    newOption.setText(option);
                    newOption.setPoll(poll);
                    return newOption;
                })
                .toList();

        poll.getOptions().clear();
        poll.getOptions().addAll(options);
        poll.setMaxVoteCount(request.getMaxVoteCount());
    }

    private void patchPollFields(Poll poll, PatchPollRequest request) {
        if (request.getTitle() != null) poll.setTitle(request.getTitle());
        if (request.getDescription() != null) poll.setDescription(request.getDescription());
        if (request.getType() != null) poll.setType(request.getType());
        if (request.getChoices() != null) {
            List<Option> options = request.getChoices().stream()
                    .map(option -> {
                        Option newOption = new Option();
                        newOption.setText(option);
                        newOption.setPoll(poll);
                        return newOption;
                    })
                    .toList();

            poll.setOptions(options);
        }
    }
}