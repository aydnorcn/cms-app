package com.aydnorcn.mis_app.dto;

import lombok.Data;

@Data
public class APIResponse<T> {

    private boolean success;
    private String message;
    private T data;

    public APIResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
}