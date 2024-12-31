package com.aydnorcn.mis_app.controller;

import com.aydnorcn.mis_app.dto.PageResponseDto;
import com.aydnorcn.mis_app.dto.vote.VoteRequest;
import com.aydnorcn.mis_app.dto.vote.VoteResponse;
import com.aydnorcn.mis_app.entity.Vote;
import com.aydnorcn.mis_app.service.VoteService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @GetMapping("/{voteId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<VoteResponse> getVote(@PathVariable String voteId) {
        return ResponseEntity.ok(new VoteResponse(voteService.getVoteById(voteId)));
    }

    @GetMapping
    public ResponseEntity<PageResponseDto<VoteResponse>> getVotes(@RequestParam(name = "poll-id", required = false) String pollId,
                                                                  @RequestParam(name = "user-id", required = false) String userId,
                                                                  @RequestParam(name = "option-id", required = false) String optionId,
                                                                  @RequestParam(name = "created-after", required = false) LocalDateTime createdAfter,
                                                                  @RequestParam(name = "created-before", required = false) LocalDateTime createdBefore,
                                                                  @RequestParam(name = "is-active", required = false) Boolean isActive,
                                                                  @RequestParam(name = "page-no", defaultValue = "0") int pageNo,
                                                                  @RequestParam(name = "page-size", defaultValue = "10") int pageSize) {

        PageResponseDto<Vote> votes = voteService.getVotes(userId, optionId, pollId, createdAfter, createdBefore, isActive, pageNo, pageSize);
        List<VoteResponse> voteResponses = votes.getContent().stream().map(VoteResponse::new).collect(Collectors.toList());

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