package com.aydnorcn.mis_app.dto.assignment;

import com.aydnorcn.mis_app.dto.AuditResponse;
import com.aydnorcn.mis_app.entity.Assignment;
import com.aydnorcn.mis_app.entity.User;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;

import java.util.List;

@Getter
public class AssignmentResponse {

    private final String id;
    private final List<String> assignedTo;
    private final String title;
    private final String content;
    private final int priority;
    private final boolean completed;

    @JsonUnwrapped
    private final AuditResponse audits;

    public AssignmentResponse(Assignment assignment) {
        this.id = assignment.getId();
        this.assignedTo = assignment.getAssignedTo().stream().map(User::getId).toList();
        this.title = assignment.getTitle();
        this.content = assignment.getContent();
        this.priority = assignment.getPriority();
        this.completed = assignment.isCompleted();
        this.audits = new AuditResponse(assignment.getCreatedAt(), assignment.getUpdatedAt(), assignment.getCreatedBy(), assignment.getUpdatedBy());
    }
}
