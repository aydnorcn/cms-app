package com.aydnorcn.mis_app.dto.option;

import com.aydnorcn.mis_app.utils.MessageConstants;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;


@Getter
public class UpdateOptionRequest {

    @NotBlank(message = MessageConstants.OPTION_TEXT_NOT_BLANK)
    private String optionText;
}