package com.aydnorcn.mis_app.controller;

import com.aydnorcn.mis_app.dto.PageResponseDto;
import com.aydnorcn.mis_app.dto.vote.VoteRequest;
import com.aydnorcn.mis_app.dto.vote.VoteResponse;
import com.aydnorcn.mis_app.entity.Vote;
import com.aydnorcn.mis_app.service.VoteService;
import com.aydnorcn.mis_app.utils.params.VoteParams;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class VoteController {

    private final VoteService voteService;

    @GetMapping("/{voteId}")
    public ResponseEntity<VoteResponse> getVote(@PathVariable String voteId) {
        return ResponseEntity.ok(new VoteResponse(voteService.getVoteById(voteId)));
    }

    @GetMapping
    public ResponseEntity<PageResponseDto<VoteResponse>> getVotes(@RequestParam Map<String, Object> searchParams) {
        VoteParams params = new VoteParams(searchParams);
        PageResponseDto<Vote> votes = voteService.getVotes(params);
        List<VoteResponse> voteResponses = votes.getContent().stream().map(VoteResponse::new).toList();

        return ResponseEntity.ok(new PageResponseDto<>(voteResponses, votes.getPageNo(), votes.getPageSize(), votes.getTotalElements(), votes.getTotalPages()));
    }

    @PostMapping
    public ResponseEntity<VoteResponse> createVote(VoteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new VoteResponse(voteService.createVote(request)));
    }

    @DeleteMapping("/{voteId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @voteService.isAuthenticatedUserOwnerOfVote(#voteId)")
    public ResponseEntity<Void> deleteVote(@PathVariable String voteId) {
        voteService.deleteVote(voteId);
        return ResponseEntity.noContent().build();
    }
}