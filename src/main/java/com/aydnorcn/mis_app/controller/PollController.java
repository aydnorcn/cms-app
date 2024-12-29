package com.aydnorcn.mis_app.controller;

import com.aydnorcn.mis_app.dto.poll.CreatePollRequest;
import com.aydnorcn.mis_app.dto.poll.PatchPollRequest;
import com.aydnorcn.mis_app.dto.poll.PollResponse;
import com.aydnorcn.mis_app.service.PollService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/polls")
@RequiredArgsConstructor
public class PollController {

    private final PollService pollService;

    @GetMapping("/{pollId}")
    public ResponseEntity<PollResponse> getPoll(@PathVariable String pollId) {
        return ResponseEntity.ok(new PollResponse(pollService.getPollById(pollId)));
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