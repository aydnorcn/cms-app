package com.aydnorcn.mis_app.controller;

import com.aydnorcn.mis_app.dto.PageResponseDto;
import com.aydnorcn.mis_app.dto.poll.CreatePollRequest;
import com.aydnorcn.mis_app.dto.poll.PatchPollRequest;
import com.aydnorcn.mis_app.dto.poll.PollResponse;
import com.aydnorcn.mis_app.entity.Poll;
import com.aydnorcn.mis_app.service.PollService;
import com.aydnorcn.mis_app.utils.PollType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/polls")
@RequiredArgsConstructor
public class PollController {

    private final PollService pollService;

    @GetMapping("/{pollId}")
    public ResponseEntity<PollResponse> getPoll(@PathVariable String pollId) {
        return ResponseEntity.ok(new PollResponse(pollService.getPollById(pollId)));
    }

    @GetMapping
    public ResponseEntity<PageResponseDto<PollResponse>> getPolls(@RequestParam(name = "poll-type", required = false) PollType type,
                                                                  @RequestParam(name = "min-option-count", required = false) Integer minOptionCount,
                                                                  @RequestParam(name = "max-option-count", required = false) Integer maxOptionCount,
                                                                  @RequestParam(name = "created-after", required = false) LocalDateTime createdAfter,
                                                                  @RequestParam(name = "created-before", required = false) LocalDateTime createdBefore,
                                                                  @RequestParam(name = "created-by", required = false) String createdBy,
                                                                  @RequestParam(name = "page-no", defaultValue = "0") int pageNo,
                                                                  @RequestParam(name = "page-size", defaultValue = "10") int pageSize) {
        PageResponseDto<Poll> polls = pollService.getPolls(type, minOptionCount, maxOptionCount, createdAfter, createdBefore, createdBy, pageNo, pageSize);
        List<PollResponse> pollResponses = polls.getContent().stream().map(PollResponse::new).collect(Collectors.toList());

        return ResponseEntity.ok(new PageResponseDto<>(pollResponses, polls.getPageNo(), polls.getPageSize(), polls.getTotalElements(), polls.getTotalPages()));
    }

    @PostMapping
    public ResponseEntity<PollResponse> createPoll(@Validated @RequestBody CreatePollRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new PollResponse(pollService.createPoll(request)));
    }

    @PutMapping("/{pollId}")
    public ResponseEntity<PollResponse> updatePoll(@PathVariable String pollId, @Validated @RequestBody CreatePollRequest request) {
        return ResponseEntity.ok(new PollResponse(pollService.updatePoll(pollId, request)));
    }

    @PatchMapping("/{pollId}")
    public ResponseEntity<PollResponse> patchPoll(@PathVariable String pollId, @RequestBody PatchPollRequest request) {
        return ResponseEntity.ok(new PollResponse(pollService.patchPoll(pollId, request)));
    }

    @DeleteMapping("/{pollId}")
    public ResponseEntity<Void> deletePoll(@PathVariable String pollId) {
        pollService.deletePoll(pollId);
        return ResponseEntity.noContent().build();
    }
}