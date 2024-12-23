package com.aydnorcn.mis_app.dto.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateRoleRequestDto {

    @NotBlank(message = "Role name cannot be empty or blank!")
    @Size(min = 3, max = 15, message = "Name length must be 3-15")
    private String name;
}
