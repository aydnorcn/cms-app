package com.aydnorcn.mis_app.dto.option;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateOptionRequest {

    @NotBlank(message = "Option text cannot be null or empty!")
    private String optionText;
}