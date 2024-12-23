package com.aydnorcn.mis_app.exception;

public class NoAuthorityException extends RuntimeException {

    public NoAuthorityException(String message) {
        super(message);
    }
}
