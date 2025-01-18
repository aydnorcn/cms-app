package com.aydnorcn.mis_app.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import static com.aydnorcn.mis_app.utils.MessageConstants.*;

@Getter
public class LoginRequest {

    @Email(message = INVALID_EMAIL_FORMAT)
    @NotBlank(message = EMAIL_NOT_BLANK)
    private String email;

    @NotBlank(message = PASSWORD_NOT_BLANK)
    private String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
