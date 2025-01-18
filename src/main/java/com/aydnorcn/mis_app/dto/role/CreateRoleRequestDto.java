package com.aydnorcn.mis_app.dto.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import static com.aydnorcn.mis_app.utils.MessageConstants.*;

@Data
public class CreateRoleRequestDto {

    @NotBlank(message = ROLE_NAME_NOT_BLANK)
    @Size(min = 3, max = 15, message = ROLE_NAME_LENGTH)
    private String name;
}
