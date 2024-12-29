package com.aydnorcn.mis_app.service;

import com.aydnorcn.mis_app.dto.poll.CreatePollRequest;
import com.aydnorcn.mis_app.dto.poll.PatchPollRequest;
import com.aydnorcn.mis_app.entity.Option;
import com.aydnorcn.mis_app.entity.Poll;
import com.aydnorcn.mis_app.exception.ResourceNotFoundException;
import com.aydnorcn.mis_app.repository.PollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PollService {

    private final PollRepository pollRepository;

    public Poll getPollById(String pollId) {
        return pollRepository.findById(pollId)
                .orElseThrow(() -> new ResourceNotFoundException("Poll not found with id: " + pollId));
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
                .collect(Collectors.toList());

        poll.getOptions().clear();
        poll.getOptions().addAll(options);
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
                    .collect(Collectors.toList());

            poll.setOptions(options);
        }
    }
}