package com.aydnorcn.mis_app.dto.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class LoginRequest {

    @NotNull(message = "Email cannot be null or empty!")
    private String email;

    @NotNull(message = "Password cannot be null or empty!")
    private String password;
}
