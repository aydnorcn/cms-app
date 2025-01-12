package com.aydnorcn.mis_app.dto.vote;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class VoteRequest {

    @NotBlank(message = "Option id cannot be null or blank")
    private String optionId;
}
