package com.aydnorcn.mis_app.controller;

import com.aydnorcn.mis_app.dto.APIResponse;
import com.aydnorcn.mis_app.dto.PageResponseDto;
import com.aydnorcn.mis_app.dto.vote.VoteRequest;
import com.aydnorcn.mis_app.dto.vote.VoteResponse;
import com.aydnorcn.mis_app.entity.Vote;
import com.aydnorcn.mis_app.exception.ErrorMessage;
import com.aydnorcn.mis_app.service.VoteService;
import com.aydnorcn.mis_app.utils.params.VoteParams;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
@Tag(name = "Vote Controller")
public class VoteController {

    private final VoteService voteService;

    @Operation(
            summary = "Retrieve vote by id"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Vote found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = VoteResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Vote not found | If given id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @GetMapping("/{voteId}")
    public ResponseEntity<APIResponse<VoteResponse>> getVote(@PathVariable String voteId) {
        return ResponseEntity
                .ok(new APIResponse<>(true, "Vote retrieved successfully", new VoteResponse(voteService.getVoteById(voteId))));
    }

    @Operation(
            summary = "Retrieve votes by filtering and pagination"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Votes found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Entity not found | If given (user, option, poll) params id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @Parameters({
            @Parameter(name = "page-no", description = "Page number", in = ParameterIn.QUERY, schema = @Schema(type = "integer")),
            @Parameter(name = "page-size", description = "Page size", in = ParameterIn.QUERY, schema = @Schema(type = "integer")),
            @Parameter(name = "sort-by", description = "Sort by", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
            @Parameter(name = "sort-order", description = "Sort order", in = ParameterIn.QUERY, schema = @Schema(type = "string", allowableValues = {"asc", "desc"})),
            @Parameter(name = "poll-id", description = "Poll id", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
            @Parameter(name = "option-id", description = "Option id", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
            @Parameter(name = "user-id", description = "User id", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
            @Parameter(name = "created-after", description = "Created after", in = ParameterIn.QUERY, schema = @Schema(type = "string", format = "date-time"), example = "2021.01.01 00:00"),
            @Parameter(name = "created-before", description = "Created before", in = ParameterIn.QUERY, schema = @Schema(type = "string", format = "date-time"), example = "2021.01.01 00:00"),
            @Parameter(name = "is-active", description = "Is voted poll active?", in = ParameterIn.QUERY, schema = @Schema(type = "boolean")),
    })
    @GetMapping
    public ResponseEntity<APIResponse<PageResponseDto<VoteResponse>>> getVotes(@RequestParam(required = false) Map<String, Object> searchParams) {
        VoteParams params = new VoteParams(searchParams);
        PageResponseDto<Vote> votes = voteService.getVotes(params);
        List<VoteResponse> voteResponses = votes.getContent().stream().map(VoteResponse::new).toList();

        return ResponseEntity.ok(
                new APIResponse<>(true, "Votes retrieved successfully",
                        new PageResponseDto<>(voteResponses, votes.getPageNo(), votes.getPageSize(), votes.getTotalElements(), votes.getTotalPages())
                ));
    }

    @Operation(
            summary = "Create a new vote"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Vote created",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = VoteResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request | If request param(s) are not valid",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Poll not found | If given poll id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            }
    )
    @PostMapping
    public ResponseEntity<APIResponse<VoteResponse>> createVote(@Validated @RequestBody VoteRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new APIResponse<>(true, "Vote created successfully", new VoteResponse(voteService.createVote(request))));
    }

    @Operation(
            summary = "Delete vote by id"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Vote deleted"),
                    @ApiResponse(responseCode = "404", description = "Vote not found | If given id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden | If user is not authorized to delete vote",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @DeleteMapping("/{voteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR') or @voteService.isAuthenticatedUserOwnerOfVote(#voteId)")
    public ResponseEntity<Void> deleteVote(@PathVariable String voteId) {
        voteService.deleteVote(voteId);
        return ResponseEntity.noContent().build();
    }
}