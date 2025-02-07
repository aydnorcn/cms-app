package com.aydnorcn.mis_app.controller;

import com.aydnorcn.mis_app.dto.PageResponseDto;
import com.aydnorcn.mis_app.dto.assignment.AssignmentResponse;
import com.aydnorcn.mis_app.dto.assignment.CreateAssignmentRequest;
import com.aydnorcn.mis_app.dto.assignment.PatchAssignmentRequest;
import com.aydnorcn.mis_app.entity.Assignment;
import com.aydnorcn.mis_app.exception.ErrorMessage;
import com.aydnorcn.mis_app.service.AssignmentService;
import com.aydnorcn.mis_app.utils.params.AssignmentParams;
import io.swagger.v3.oas.annotations.Operation;
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
    public ResponseEntity<AssignmentResponse> getAssignmentById(@PathVariable String assignmentId) {
        return ResponseEntity.ok(new AssignmentResponse(assignmentService.getAssignmentById(assignmentId)));
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
    @GetMapping
    public ResponseEntity<PageResponseDto<AssignmentResponse>> getAssignments(@RequestParam Map<String, Object> searchParams) {
        AssignmentParams params = new AssignmentParams(searchParams);

        PageResponseDto<Assignment> assignments = assignmentService.getAssignments(params);
        List<AssignmentResponse> assignmentResponses = assignments.getContent().stream().map(AssignmentResponse::new).toList();

        return ResponseEntity.ok(
                new PageResponseDto<>(assignmentResponses, assignments.getPageNo(), assignments.getPageSize(), assignments.getTotalElements(), assignments.getTotalPages())
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<AssignmentResponse> createAssignment(@Validated @RequestBody CreateAssignmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new AssignmentResponse(assignmentService.createAssignment(request)));
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<AssignmentResponse> updateAssignment(@PathVariable String assignmentId, @Validated @RequestBody CreateAssignmentRequest request) {
        return ResponseEntity.ok(new AssignmentResponse(assignmentService.updateAssignment(assignmentId, request)));
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<AssignmentResponse> patchAssignment(@PathVariable String assignmentId, @Validated @RequestBody PatchAssignmentRequest request) {
        return ResponseEntity.ok(new AssignmentResponse(assignmentService.patchAssignment(assignmentId, request)));
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Void> deleteAssignment(@PathVariable String assignmentId) {
        assignmentService.deleteAssignment(assignmentId);
        return ResponseEntity.noContent().build();
    }
}