package com.aydnorcn.mis_app.dto.option;

import lombok.Getter;

@Getter
public class CreateOptionRequest {

    //TODO: Validations

    private String optionText;
    private String pollId;
}