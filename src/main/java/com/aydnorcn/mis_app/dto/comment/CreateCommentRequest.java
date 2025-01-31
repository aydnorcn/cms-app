package com.aydnorcn.mis_app.dto.comment;

import com.aydnorcn.mis_app.utils.MessageConstants;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCommentRequest {

    @NotBlank(message = MessageConstants.CONTENT_NOT_BLANK)
    private String content;
}