package com.aydnorcn.mis_app.dto.category;

import com.aydnorcn.mis_app.utils.MessageConstants;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateCategoryRequest {

    @NotBlank(message = MessageConstants.CATEGORY_NAME_NOT_BLANK)
    private String name;
}
