package com.aydnorcn.mis_app.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginRequest {

    @NotBlank(message = "Email cannot be null or empty!")
    private String email;

    @NotBlank(message = "Password cannot be null or empty!")
    private String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
