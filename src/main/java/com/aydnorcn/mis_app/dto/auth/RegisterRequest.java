package com.aydnorcn.mis_app.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import static com.aydnorcn.mis_app.utils.MessageConstants.*;

@Getter
public class RegisterRequest {

    @Email(message = INVALID_EMAIL_FORMAT)
    @NotBlank(message = EMAIL_NOT_BLANK)
    private String email;

    @Size(min = 8, max = 30, message = PASSWORD_LENGTH)
    @NotBlank(message = PASSWORD_NOT_BLANK)
    private String password;

    @NotBlank(message = FIRSTNAME_NOT_BLANK)
    private String firstName;
    @NotBlank(message = LASTNAME_NOT_BLANK)
    private String lastName;

    public RegisterRequest(String email, String password, String firstName, String lastName) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
