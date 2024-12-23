package com.aydnorcn.mis_app.exception;

public class AlreadyExistsException extends RuntimeException
{
    public AlreadyExistsException(String message) {
        super(message);
    }
}
