package com.aydnorcn.mis_app.controller;

import com.aydnorcn.mis_app.dto.PageResponseDto;
import com.aydnorcn.mis_app.dto.assignment.AssignmentResponse;
import com.aydnorcn.mis_app.dto.assignment.CreateAssignmentRequest;
import com.aydnorcn.mis_app.dto.assignment.PatchAssignmentRequest;
import com.aydnorcn.mis_app.entity.Assignment;
import com.aydnorcn.mis_app.service.AssignmentService;
import com.aydnorcn.mis_app.utils.params.AssignmentParams;
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
public class AssignmentController {

    private final AssignmentService assignmentService;

    @GetMapping("/{assignmentId}")
    public ResponseEntity<AssignmentResponse> getAssignmentById(@PathVariable String assignmentId) {
        return ResponseEntity.ok(new AssignmentResponse(assignmentService.getAssignmentById(assignmentId)));
    }

    @GetMapping
    public ResponseEntity<PageResponseDto<AssignmentResponse>> getAssignments(@RequestParam Map<String, Object> searchParams) {
        AssignmentParams params = new AssignmentParams(searchParams);

        PageResponseDto<Assignment> assignments = assignmentService.getAssignments(params);
        List<AssignmentResponse> assignmentResponses = assignments.getContent().stream().map(AssignmentResponse::new).toList();

        return ResponseEntity.ok(
                new PageResponseDto<>(assignmentResponses, assignments.getPageNo(), assignments.getPageSize(), assignments.getTotalElements(), assignments.getTotalPages())
        );
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<AssignmentResponse> createAssignment(@Validated @RequestBody CreateAssignmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new AssignmentResponse(assignmentService.createAssignment(request)));
    }

    @PutMapping("/{assignmentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<AssignmentResponse> updateAssignment(@PathVariable String assignmentId, @Validated @RequestBody CreateAssignmentRequest request) {
        return ResponseEntity.ok(new AssignmentResponse(assignmentService.updateAssignment(assignmentId, request)));
    }

    @PatchMapping("/{assignmentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<AssignmentResponse> patchAssignment(@PathVariable String assignmentId, @Validated @RequestBody PatchAssignmentRequest request) {
        return ResponseEntity.ok(new AssignmentResponse(assignmentService.patchAssignment(assignmentId, request)));
    }

    @DeleteMapping("/{assignmentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Void> deleteAssignment(@PathVariable String assignmentId) {
        assignmentService.deleteAssignment(assignmentId);
        return ResponseEntity.noContent().build();
    }
}