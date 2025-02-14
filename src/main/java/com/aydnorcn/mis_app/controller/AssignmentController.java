package com.aydnorcn.mis_app.controller;

import com.aydnorcn.mis_app.dto.APIResponse;
import com.aydnorcn.mis_app.dto.PageResponseDto;
import com.aydnorcn.mis_app.dto.assignment.AssignmentResponse;
import com.aydnorcn.mis_app.dto.assignment.CreateAssignmentRequest;
import com.aydnorcn.mis_app.dto.assignment.PatchAssignmentRequest;
import com.aydnorcn.mis_app.entity.Assignment;
import com.aydnorcn.mis_app.exception.ErrorMessage;
import com.aydnorcn.mis_app.service.AssignmentService;
import com.aydnorcn.mis_app.utils.params.AssignmentParams;
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
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
@Tag(name = "Assignment Controller")
public class AssignmentController {

    private final AssignmentService assignmentService;

    @Operation(
            summary = "Retrieve assignment by id"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Assignment found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AssignmentResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Assignment not found | If given id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @GetMapping("/{assignmentId}")
    public ResponseEntity<APIResponse<AssignmentResponse>> getAssignmentById(@PathVariable String assignmentId) {
        return ResponseEntity
                .ok(new APIResponse<>(true, "Assignment retrieved successfully", new AssignmentResponse(assignmentService.getAssignmentById(assignmentId))));
    }

    @Operation(
            summary = "Retrieve assignments by filtering and pagination"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Assignments retrieved",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Entity not found | If given (assignedTo, event, createdBy) params id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            }
    )
    @Parameters({
            @Parameter(name = "page-no", in = ParameterIn.QUERY, description = "Page number", schema = @Schema(type = "integer")),
            @Parameter(name = "page-size", in = ParameterIn.QUERY, description = "Page size", schema = @Schema(type = "integer")),
            @Parameter(name = "sort-by", description = "Sort by", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
            @Parameter(name = "sort-order", description = "Sort order", in = ParameterIn.QUERY, schema = @Schema(type = "string", allowableValues = {"asc", "desc"})),
            @Parameter(name = "assigned-to", description = "Assigned to", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
            @Parameter(name = "event-id", description = "Event id", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
            @Parameter(name = "is-completed", description = "Is assignment completed?", in = ParameterIn.QUERY, schema = @Schema(type = "boolean")),
            @Parameter(name = "min-priority", description = "Min priority of assignment", in = ParameterIn.QUERY, schema = @Schema(type = "integer")),
            @Parameter(name = "max-priority", description = "Max priority of assignment", in = ParameterIn.QUERY, schema = @Schema(type = "integer")),
            @Parameter(name = "created-after", description = "Assignment(s) created after", in = ParameterIn.QUERY, schema = @Schema(type = "string", format = "date-time"), example = "2021.01.01 00:00"),
            @Parameter(name = "created-before", description = "Assignment(s) created before", in = ParameterIn.QUERY, schema = @Schema(type = "string", format = "date-time"), example = "2021.01.01 00:00"),
            @Parameter(name = "created-by", description = "Assignment(s) created by", in = ParameterIn.QUERY, schema = @Schema(type = "string")),

    })
    @GetMapping
    public ResponseEntity<APIResponse<PageResponseDto<AssignmentResponse>>> getAssignments(@RequestParam(required = false) Map<String, Object> searchParams) {
        AssignmentParams params = new AssignmentParams(searchParams);

        PageResponseDto<Assignment> assignments = assignmentService.getAssignments(params);
        List<AssignmentResponse> assignmentResponses = assignments.getContent().stream().map(AssignmentResponse::new).toList();

        return ResponseEntity.ok(
                new APIResponse<>(true, "Assignments retrieved successfully",
                        new PageResponseDto<>(assignmentResponses, assignments.getPageNo(), assignments.getPageSize(), assignments.getTotalElements(), assignments.getTotalPages()))
        );
    }

    @Operation(
            summary = "Create a new assignment"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Assignment created",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AssignmentResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request | If request value(s) are not valid",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Not have permission to access | If user not admin or moderator",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Entity not found | If given (assignedTo, event, createdBy) params id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            }
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<APIResponse<AssignmentResponse>> createAssignment(@Validated @RequestBody CreateAssignmentRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new APIResponse<>(true, "Assignment created successfully", new AssignmentResponse(assignmentService.createAssignment(request))));
    }

    @Operation(
            summary = "Update all required parts of assignment"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Assignment updated",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AssignmentResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request | If request value(s) are not valid",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Not have permission to access | If user not admin or moderator",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Entity not found | If given (assignedTo, event, createdBy) params id or assignment id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PutMapping("/{assignmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<APIResponse<AssignmentResponse>> updateAssignment(@PathVariable String assignmentId, @Validated @RequestBody CreateAssignmentRequest request) {
        return ResponseEntity
                .ok(new APIResponse<>(true, "Assignment updated successfully", new AssignmentResponse(assignmentService.updateAssignment(assignmentId, request))));
    }

    @Operation(
            summary = "Update partial parts of assignment"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Assignment patched",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AssignmentResponse.class))),
                    @ApiResponse(responseCode = "403", description = "Not have permission to access | If user not admin or moderator",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Entity not found | If given (assignedTo, event, createdBy) params id or assignment id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PatchMapping("/{assignmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<APIResponse<AssignmentResponse>> patchAssignment(@PathVariable String assignmentId, @Validated @RequestBody PatchAssignmentRequest request) {
        return ResponseEntity
                .ok(new APIResponse<>(true, "Assignment patched successfully", new AssignmentResponse(assignmentService.patchAssignment(assignmentId, request))));
    }

    @Operation(
            summary = "Delete assignment by giving id"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Assignment deleted"),
                    @ApiResponse(responseCode = "403", description = "Not have permission to access | If user not admin or moderator",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Entity not found | If assignment id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @DeleteMapping("/{assignmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Void> deleteAssignment(@PathVariable String assignmentId) {
        assignmentService.deleteAssignment(assignmentId);
        return ResponseEntity.noContent().build();
    }
}