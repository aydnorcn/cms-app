package com.aydnorcn.mis_app.dto.assignment;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PatchAssignmentRequest {


    @Size(min = 1, message = "Assigned to must have at least 1 user")
    private List<String> assignedTo;

    private String eventId;
    private String title;
    private String content;

    @Min(value = 1, message = "Priority must be minimum 1")
    @Max(value = 5, message = "Priority must be maximum 5")
    private Integer priority;
}
