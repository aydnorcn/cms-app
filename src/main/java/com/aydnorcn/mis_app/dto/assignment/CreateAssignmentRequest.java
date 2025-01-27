package com.aydnorcn.mis_app.dto.assignment;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateAssignmentRequest {

    @NotNull(message = "Assigned to is required")
    @Size(min = 1, message = "Assigned to must have at least 1 user")
    private List<String> assignedTo;

    @NotBlank(message = "Event id is required")
    private String eventId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    @NotNull(message = "Priority is required")
    @Min(value = 1, message = "Priority must be minimum 1")
    @Max(value = 5, message = "Priority must be maximum 5")
    private int priority;
}