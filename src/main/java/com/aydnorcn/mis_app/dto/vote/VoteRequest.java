package com.aydnorcn.mis_app.dto.vote;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import static com.aydnorcn.mis_app.utils.MessageConstants.*;

@Getter
@Setter
public class VoteRequest {

    @NotBlank(message = OPTION_ID_NOT_BLANK)
    private String optionId;
}
