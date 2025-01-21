package com.aydnorcn.mis_app.exception;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public record ErrorMessage(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss", timezone = "GMT+3") Date timestamp,
        String message) {
}
