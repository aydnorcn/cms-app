package com.aydnorcn.mis_app.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RegisterRequest {

    @NotBlank(message = "Email cannot be null or empty!")
    private String email;
    @NotBlank(message = "Password cannot be null or empty!")
    private String password;

    @NotBlank(message = "Firstname cannot be null or empty!")
    private String firstName;
    @NotBlank(message = "Lastname cannot be null or empty!")
    private String lastName;
}
