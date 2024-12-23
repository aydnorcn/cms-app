package com.aydnorcn.mis_app.exception;

import lombok.Getter;

import java.util.Date;

@Getter
public class ErrorMessage {

    private final Date timestamp;
    private final String message;

    public ErrorMessage(Date timestamp, String message) {
        this.timestamp = timestamp;
        this.message = message;
    }
}
