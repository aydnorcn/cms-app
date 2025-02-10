package com.aydnorcn.mis_app.controller;

import com.aydnorcn.mis_app.dto.APIResponse;
import com.aydnorcn.mis_app.dto.PageResponseDto;
import com.aydnorcn.mis_app.dto.poll.CreatePollRequest;
import com.aydnorcn.mis_app.dto.poll.PatchPollRequest;
import com.aydnorcn.mis_app.dto.poll.PollResponse;
import com.aydnorcn.mis_app.entity.Poll;
import com.aydnorcn.mis_app.exception.ErrorMessage;
import com.aydnorcn.mis_app.service.PollService;
import com.aydnorcn.mis_app.utils.params.PollParams;
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
@RequestMapping("/api/polls")
@RequiredArgsConstructor
@Tag(name = "Polls")
public class PollController {

    private final PollService pollService;

    @Operation(
            summary = "Retrieve poll by id"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Poll found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PollResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Poll not found | If given id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @GetMapping("/{pollId}")
    public ResponseEntity<APIResponse<PollResponse>> getPoll(@PathVariable String pollId) {
        return ResponseEntity
                .ok(new APIResponse<>(true, "Poll retrieves successfully", new PollResponse(pollService.getPollById(pollId))));
    }

    @Operation(
            summary = "Retrieve posts by filtering and pagination"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Polls found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PollResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Entity not found | If given (user) params id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @Parameters({
            @Parameter(name = "page-no", description = "Page number", in = ParameterIn.QUERY),
            @Parameter(name = "page-size", description = "Page size", in = ParameterIn.QUERY),
            @Parameter(name = "sort-by", description = "Sort by", in = ParameterIn.QUERY),
            @Parameter(name = "sort-order", description = "Sort order", in = ParameterIn.QUERY),
            @Parameter(name = "poll-type", description = "Type of poll", in = ParameterIn.QUERY),
            @Parameter(name = "min-option-count", description = "Min option count of poll", in = ParameterIn.QUERY),
            @Parameter(name = "max-option-count", description = "Max option count of poll", in = ParameterIn.QUERY),
            @Parameter(name = "created-after", description = "Poll created after given date", in = ParameterIn.QUERY),
            @Parameter(name = "created-before", description = "Poll created before given date", in = ParameterIn.QUERY),
            @Parameter(name = "created-by", description = "Poll created by given user id", in = ParameterIn.QUERY),
    })
    @GetMapping
    public ResponseEntity<APIResponse<PageResponseDto<PollResponse>>> getPolls(@RequestParam(required = false) Map<String, Object> searchParams) {
        PollParams params = new PollParams(searchParams);
        PageResponseDto<Poll> polls = pollService.getPolls(params);
        List<PollResponse> pollResponses = polls.getContent().stream().map(PollResponse::new).toList();

        return ResponseEntity.ok(
                new APIResponse<>(true, "Polls retrieved successfully",
                        new PageResponseDto<>(pollResponses, polls.getPageNo(), polls.getPageSize(), polls.getTotalElements(), polls.getTotalPages())
                ));
    }

    @Operation(
            summary = "Create a new poll"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Poll created",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PollResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Bad request | If request param(s) are not valid",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden | If user is not authorized to create poll",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'ORGANIZATOR')")
    public ResponseEntity<APIResponse<PollResponse>> createPoll(@Validated @RequestBody CreatePollRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new APIResponse<>(true, "Poll created successfully", new PollResponse(pollService.createPoll(request))));
    }

    @Operation(
            summary = "Update all required parts of poll"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Poll updated",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PollResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Poll not found | If given id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Bad request | If request param(s) are not valid",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden | If user is not authorized to update poll",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PutMapping("/{pollId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'ORGANIZATOR')")
    public ResponseEntity<APIResponse<PollResponse>> updatePoll(@PathVariable String pollId, @Validated @RequestBody CreatePollRequest request) {
        return ResponseEntity
                .ok(new APIResponse<>(true, "Poll updated successfully", new PollResponse(pollService.updatePoll(pollId, request))));
    }

    @Operation(
            summary = "Update partial parts of poll"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Poll updated",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PollResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Poll not found | If given id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden | If user is not authorized to update poll",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PatchMapping("/{pollId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'ORGANIZATOR')")
    public ResponseEntity<APIResponse<PollResponse>> patchPoll(@PathVariable String pollId, @RequestBody PatchPollRequest request) {
        return ResponseEntity
                .ok(new APIResponse<>(true, "Poll updated successfully", new PollResponse(pollService.patchPoll(pollId, request))));
    }

    @Operation(
            summary = "Delete poll by id"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Poll deleted"),
                    @ApiResponse(responseCode = "404", description = "Poll not found | If given id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden | If user is not authorized to delete poll",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @DeleteMapping("/{pollId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'ORGANIZATOR')")
    public ResponseEntity<Void> deletePoll(@PathVariable String pollId) {
        pollService.deletePoll(pollId);
        return ResponseEntity.noContent().build();
    }
}