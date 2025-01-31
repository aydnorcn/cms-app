package com.aydnorcn.mis_app.dto.post;

import com.aydnorcn.mis_app.utils.MessageConstants;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreatePostRequest {

    @NotBlank(message = MessageConstants.TITLE_NOT_BLANK)
    private String title;

    @NotBlank(message = MessageConstants.CONTENT_NOT_BLANK)
    private String content;

    @NotBlank(message = MessageConstants.CATEGORY_NOT_BLANK)
    private String categoryId;
}