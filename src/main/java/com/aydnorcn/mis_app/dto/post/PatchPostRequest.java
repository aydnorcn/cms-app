package com.aydnorcn.mis_app.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PatchPostRequest {

    private String title;
    private String content;
    private String categoryId;
}