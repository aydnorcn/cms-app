package com.aydnorcn.mis_app.dto.option;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import static com.aydnorcn.mis_app.utils.MessageConstants.*;

@Getter
@Setter
public class CreateOptionRequest {

    @NotBlank(message = OPTION_TEXT_NOT_BLANK)
    private String optionText;

    @NotBlank(message = POLL_ID_NOT_BLANK)
    private String pollId;
}