package com.aydnorcn.mis_app.exception;

public class RateLimitExceedException extends RuntimeException {
    public RateLimitExceedException(String message) {
        super(message);
    }
}
